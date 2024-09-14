package com.example.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import com.example.demo.entity.User;
import com.example.demo.reposatory.UserReposatory;

import java.util.ArrayList;
import java.util.Optional;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    UserReposatory userReposatory;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> oUser = userReposatory.findByEmail(username);

        if (oUser.isEmpty()) {
            throw new UsernameNotFoundException("User not found");
        }

        User u = oUser.get();

        // Create a Spring Security UserDetails object without roles
        return new org.springframework.security.core.userdetails.User(
                u.getEmail(),
                u.getPassword(),
                new ArrayList<>());
    }
}
