package com.example.demo.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.demo.entity.User;

import java.util.ArrayList;

@Service
public class MyUserDetailsService implements UserDetailsService {

    private String email;
    private String password;

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        return new org.springframework.security.core.userdetails.User(this.email, this.password, new ArrayList<>());
    }

    public void getUserDetails(User u) {
        this.email = u.getEmail();
        this.password = u.getPassword();
    }
}