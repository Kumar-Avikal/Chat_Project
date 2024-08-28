package com.example.demo.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.UserDTO;
import com.example.demo.entity.User;
import com.example.demo.reposatory.UserReposatory;
import com.example.demo.service.UserService;

@RestController
// @RequestMapping("example/api")
public class UserController {

    @Autowired
    UserReposatory userReposatory;
    @Autowired
    UserService userService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/getAllUser")
    public List<UserDTO> getAllUser() {
        List<User> userList = userReposatory.findAll();
        List<UserDTO> users = new ArrayList<UserDTO>();

        for (User u : userList) {
            UserDTO user = userService.mapToDTO(u);
            users.add(user);
        }
        return users;
    }

    @GetMapping("/getUser/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        System.out.println("the id is {1}");
        return userService.getUserById(id);
    }

    @PostMapping("/saveUser")
    public ResponseEntity<?> saveUser(@RequestBody UserDTO userDTO) {
        return userService.saveUser(userDTO);
    }

}
