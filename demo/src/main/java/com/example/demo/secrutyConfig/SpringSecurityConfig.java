package com.example.demo.secrutyConfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfig {

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                http
                                .csrf(csrf -> csrf.disable())
                                .authorizeHttpRequests(authorize -> authorize
                                                .requestMatchers("api/public/**", "api/login", "api/error",
                                                                "api/saveUser", "api/getAllUser", "api/getUser/{id}")
                                                .permitAll()
                                                .anyRequest().authenticated())
                                .formLogin(form -> form
                                                .loginPage("/api/login") // Updated login page path
                                                .defaultSuccessUrl("/")
                                                .permitAll())
                                .logout(logout -> logout
                                                .logoutSuccessUrl("/api/logOut") // Updated logout success URL
                                                .permitAll());

                return http.build();
        }

}
