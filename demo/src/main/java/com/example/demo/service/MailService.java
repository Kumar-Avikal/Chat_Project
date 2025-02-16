package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class MailService {

    @Autowired
    private JavaMailSender mailSender;

    private final String email = "your-email@gmail.com";
    private final Map<String, String> otpStorage = new HashMap<>();

    public String sendOtpEmail(String toEmail) {
        String otp = generateOtp();
        otpStorage.put(toEmail, otp);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(this.email);
        message.setTo(toEmail);
        message.setSubject("Your OTP for Login");
        message.setText("Your OTP code is: " + otp + ". It is valid for 5 minutes.");

        mailSender.send(message);
        System.out.println("OTP sent to: " + toEmail);
        return otp;
    }

    public boolean validateOtp(String email, String otp) {
        String storedOtp = otpStorage.get(email);
        if (storedOtp != null && storedOtp.equals(otp)) {
            otpStorage.remove(email);
            return true;
        }
        return false;
    }

    private String generateOtp() {
        Random random = new Random();
        int otp = 100000 + random.nextInt(900000);
        return String.valueOf(otp);
    }
}