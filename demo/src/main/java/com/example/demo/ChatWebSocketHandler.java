package com.example.demo;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.example.demo.entity.ChatMessage;
import com.example.demo.entity.User;
import com.example.demo.reposatory.ChatMessageReposatory;
import com.example.demo.reposatory.UserReposatory;

public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final JwtUtil jwtUtil;
    private Map<String, WebSocketSession> activeSessions = new ConcurrentHashMap<>();

    public ChatWebSocketHandler(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Autowired
    private UserReposatory userReposatory;

    @Autowired
    private ChatMessageReposatory chatMessageReposatory;

    String userName = "";

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String token = getJwtFromSession(session);
        String username = jwtUtil.extractUsername(token);
        Optional<User> user = userReposatory.findByEmail(username);
        if (user.isPresent()) {
            User u = user.get();
            this.userName = u.getUserName();
        }
        if (token != null && jwtUtil.validateToken(token, username)) {
            activeSessions.put(username, session);
            System.out.println("Connection established for user: " + userName);
        } else {
            session.close(CloseStatus.NOT_ACCEPTABLE.withReason("Invalid JWT token"));
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        activeSessions.values().remove(session);
        System.out.println("Connection closed: " + session.getPrincipal().getName());
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        String senderName = session.getPrincipal().getName();

        // Extract recipient name or id from the payload (you can customize this)
        String recipient = (payload);
        WebSocketSession recipientSession = activeSessions.get(recipient);

        // Save the message in the database
        Optional<User> sender = userReposatory.findByEmail(senderName);
        Optional<User> recipientUser = userReposatory.findByEmail(recipient);

        if (sender.isPresent() && recipientUser.isPresent()) {
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setSenderId(sender.get().getId());
            chatMessage.setRecipientId(recipientUser.get().getId());
            chatMessage.setMessage(payload); // or extract the actual message content if payload has more data
            chatMessage.setTimeStamp(LocalDateTime.now());
            chatMessageReposatory.save(chatMessage);
        }

        // Forward the message to the recipient if the session is active
        if (recipientSession != null && recipientSession.isOpen()) {
            recipientSession.sendMessage(new TextMessage(senderName + ": " + payload));
        }
    }

    private String getJwtFromSession(WebSocketSession session) {
        return session.getHandshakeHeaders().getFirst("Authorization");
    }
}