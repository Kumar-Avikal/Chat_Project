package com.example.demo.service;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.demo.JwtUtil;
import com.example.demo.dto.InvestmentDetails;
import com.example.demo.dto.UserDTO;
import com.example.demo.entity.PhoneNumber;
import com.example.demo.entity.User;
import com.example.demo.reposatory.PhoneReposatory;
import com.example.demo.reposatory.UserReposatory;
import com.example.demo.resources.ApiResponse;

@Service
public class UserService {

    @Autowired
    private UserReposatory userReposatory;

    @Autowired
    private PhoneReposatory phoneReposatory;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private MailService mailService;

    public UserDTO mapToDTO(User u) {
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail(u.getEmail());
        userDTO.setId(u.getId());
        userDTO.setCreatedAt(u.getCreatedAt());
        userDTO.setFirstName(u.getFirstName());
        userDTO.setMobileNumber(u.getMobileNumber());
        userDTO.setLastName(u.getLastName());
        userDTO.setInvestmentDetails(u.getInvestmentDetails());
        return userDTO;
    }

    public User mapToEntity(UserDTO u) {
        User user = new User();
        user.setEmail(u.getEmail());
        user.setPassword(bCryptPasswordEncoder.encode(u.getPassword()));
        user.setFirstName(u.getFirstName());
        user.setMobileNumber(u.getMobileNumber());
        user.setLastName(u.getLastName());
        user.setInvestmentDetails(u.getInvestmentDetails());
        return user;
    }

    public ResponseEntity<?> saveUser(UserDTO userDTO) {
        Optional<User> oUser = userReposatory.findByEmail(userDTO.getEmail());
        if (oUser.isPresent()) {
            ApiResponse response = new ApiResponse(
                    HttpStatus.OK.value(),
                    "Email already registered, please log in",
                    null);
            return ResponseEntity.ok(response);
        }

        User user = mapToEntity(userDTO);
        user.setCreatedAt(LocalDate.now());
        userReposatory.save(user);

        // mailService.sendOtpEmail(user.getEmail());

        ApiResponse response = new ApiResponse(
                HttpStatus.OK.value(),
                "User saved successfully",
                user);
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<?> getUserById(Long id) {
        Optional<User> oUser = userReposatory.findById(id);
        if (oUser.isPresent()) {
            User u = oUser.get();
            UserDTO userDTO = mapToDTO(u);

            ApiResponse response = new ApiResponse(
                    HttpStatus.OK.value(),
                    "User found",
                    userDTO);
            return ResponseEntity.ok(response);
        }

        ApiResponse response = new ApiResponse(
                HttpStatus.NOT_FOUND.value(),
                "No such user found",
                null);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    public ResponseEntity<?> login(String email, String password) {
        Optional<User> oUser = userReposatory.findByEmail(email);
        if (!oUser.isPresent()) {
            ApiResponse response = new ApiResponse(
                    HttpStatus.UNAUTHORIZED.value(),
                    "Invalid email or password",
                    null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        User user = oUser.get();
        if (!bCryptPasswordEncoder.matches(password, user.getPassword())) {
            ApiResponse response = new ApiResponse(
                    HttpStatus.UNAUTHORIZED.value(),
                    "Invalid email or password",
                    null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        String token = jwtUtil.generateToken(user);
        user.setJWT(token);
        userReposatory.save(user);
        user.setPassword(null);
        ApiResponse response = new ApiResponse(
                HttpStatus.OK.value(),
                "Login successful",
                user);
        System.out.println("Login successful: " + response);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    public ResponseEntity<?> saveNumber(PhoneNumber phoneNumber) {
        this.phoneReposatory.save(phoneNumber);
        return ResponseEntity.status(HttpStatus.OK).body("number saved successfully");
    }

    public ResponseEntity<?> saveOffering(InvestmentDetails investmentDetails, String email) {
        Optional<User> oUser = userReposatory.findByEmail(email);
        if (oUser.isPresent()) {
            User user = oUser.get();
            user.setInvestmentDetails(investmentDetails);
            userReposatory.save(user);
            ApiResponse response = new ApiResponse(
                    HttpStatus.OK.value(),
                    "Investment detailes saved",
                    investmentDetails);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        }
        ApiResponse response = new ApiResponse(
                HttpStatus.OK.value(),
                "Investment detailes not Saved",
                investmentDetails);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    public ResponseEntity<?> sendOtp(String email) {
        Optional<User> oUser = userReposatory.findByEmail(email);
        if (oUser.isPresent()) {
            mailService.sendOtpEmail(email);
            return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(), "OTP sent successfully", null));
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse(HttpStatus.NOT_FOUND.value(), "User not found", null));
    }

    public ResponseEntity<?> verifyOtpAndLogin(String email, String otp) {
        if (mailService.validateOtp(email, otp)) {
            Optional<User> oUser = userReposatory.findByEmail(email);
            if (oUser.isPresent()) {
                User user = oUser.get();
                String token = jwtUtil.generateToken(user);
                user.setJWT(token);
                userReposatory.save(user);
                user.setPassword(null);
                return ResponseEntity.ok(new ApiResponse(HttpStatus.OK.value(), "Login successful", user));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(HttpStatus.NOT_FOUND.value(), "User not found", null));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse(HttpStatus.UNAUTHORIZED.value(), "Invalid OTP", null));
    }

}