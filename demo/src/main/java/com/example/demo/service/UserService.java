package com.example.demo.service;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
// import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.JwtUtil;
import com.example.demo.dto.AuthenticationResponse;
import com.example.demo.dto.UserDTO;
import com.example.demo.entity.User;
import com.example.demo.reposatory.UserReposatory;

@Service
public class UserService {
    @Autowired
    private UserReposatory userReposatory;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public UserDTO mapToDTO(User u) {
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail(u.getEmail());
        userDTO.setId(u.getId());
        userDTO.setCreatedAt(u.getCreatedAt());
        userDTO.setUserName(u.getUserName());
        userDTO.setRole(u.getRole());
        return userDTO;
    }

    public User mapToEntity(UserDTO u) {
        User user = new User();
        user.setEmail(u.getEmail());
        user.setPassword(bCryptPasswordEncoder.encode(u.getPassword()));
        user.setUserName(u.getUserName());
        user.setRole(u.getRole());
        return user;
    }

    public ResponseEntity<?> saveUser(UserDTO userDTO) {
        Optional<User> oUser = userReposatory.findByEmail(userDTO.getEmail());
        if (oUser.isPresent()) {
            return ResponseEntity.ok().body("email already registered please log-in");
        }
        User user = mapToEntity(userDTO);
        user.setCreatedAt(LocalDate.now());
        userReposatory.save(user);
        return ResponseEntity.ok().body("User Saved Successfully");
    }

    public ResponseEntity<?> getUserById(Long id) {
        Optional<User> oUser = userReposatory.findById(id);
        if (oUser.isPresent()) {
            User u = oUser.get();
            UserDTO userDTO = mapToDTO(u);
            return ResponseEntity.ok().body(userDTO);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No Such User Found");
    }

    public ResponseEntity<?> login(String email, String password) {
        Optional<User> oUser = userReposatory.findByEmail(email);
        if (!oUser.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        }

        User user = oUser.get();

        if (!bCryptPasswordEncoder.matches(password, user.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password");
        }

        String token = jwtUtil.generateToken(user);

        UserDTO loggedInUser = mapToDTO(user);
        return ResponseEntity.ok().body(new AuthenticationResponse(loggedInUser, token));
    }
}