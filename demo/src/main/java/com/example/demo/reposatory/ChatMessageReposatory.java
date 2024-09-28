package com.example.demo.reposatory;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.ChatMessage;

@Repository
public interface ChatMessageReposatory extends JpaRepository<ChatMessage, Long> {

}
