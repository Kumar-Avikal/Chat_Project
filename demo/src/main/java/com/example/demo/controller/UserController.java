package com.example.demo.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dto.InvestmentDetails;
import com.example.demo.dto.UserCred;
import com.example.demo.dto.UserDTO;
import com.example.demo.entity.PhoneNumber;
import com.example.demo.entity.User;
import com.example.demo.reposatory.UserReposatory;
import com.example.demo.resources.ApiResponse;
import com.example.demo.service.MailService;
import com.example.demo.service.UserService;

@RestController
@RequestMapping("api")
public class UserController {

    @Autowired
    UserReposatory userReposatory;
    @Autowired
    UserService userService;
    @Autowired
    MailService mailService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserCred cred) {
        return userService.login(cred.getEmail(), cred.getPassword());
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

    @PostMapping("/saveNumber")
    public ResponseEntity<?> saveNumber(@RequestBody PhoneNumber phoneNumber) {
        return userService.saveNumber(phoneNumber);
    }

    @PostMapping("/saveOffering")
    public ResponseEntity<?> saveOffering(@RequestBody InvestmentDetails investmentDetails,
            @RequestParam String email) {
        return userService.saveOffering(investmentDetails, email);
    }

    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        if (email == null || email.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(HttpStatus.BAD_REQUEST.value(), "Email is required", null));
        }
        return userService.sendOtp(email);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestParam String email, @RequestParam String otp) {
        return userService.verifyOtpAndLogin(email, otp);
    }

}
