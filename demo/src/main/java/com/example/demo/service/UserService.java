package com.example.demo.service;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.example.demo.dto.UserDTO;
import com.example.demo.entity.User;
import com.example.demo.reposatory.UserReposatory;

@Service
public class UserService {
    @Autowired
    private UserReposatory userReposatory;

    public UserDTO mapToDTO(User u) {
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail(u.getEmail());
        userDTO.setId(u.getId());
        // userDTO.setPassword(u.getPassword());
        userDTO.setCreatedAt(u.getCreatedAt());
        userDTO.setUserName(u.getUserName());

        return userDTO;
    }

    public User mapToEntity(UserDTO u) {
        User user = new User();
        user.setEmail(u.getEmail());
        user.setId(u.getId());
        user.setPassword(u.getPassword());
        user.setCreatedAt(u.getCreatedAt());
        user.setUserName(u.getUserName());

        return user;
    }

    public ResponseEntity<?> saveUser(UserDTO userDTO) {
        User user = mapToEntity(userDTO);
        user.setCreatedAt(LocalDate.now());
        return ResponseEntity.ok().body(userDTO + "User Saved Successfully");
    }

    public ResponseEntity<?> getUserById(Long id) {
        Optional<User> oUser = userReposatory.findById(id);
        if (oUser.isPresent()) {
            User u = oUser.get();
            UserDTO user = mapToDTO(u);
            return ResponseEntity.ok().body(user);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No Such User Found");
    }
}
