package com.example.demo.secrutyConfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import com.example.demo.JwtRequestFilter;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfig {

        private final JwtRequestFilter jwtRequestFilter;

        public SpringSecurityConfig(JwtRequestFilter jwtRequestFilter) {
                this.jwtRequestFilter = jwtRequestFilter;
        }

        @Bean

        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                http
                                .cors(cors -> cors.configurationSource(
                                                request -> new CorsConfiguration().applyPermitDefaultValues()))
                                .csrf(csrf -> csrf.disable())
                                .authorizeHttpRequests(authorize -> authorize
                                                .requestMatchers(HttpMethod.POST, "api/**").permitAll()
                                                .requestMatchers("/api/public/**", "/api/login", "/api/error",
                                                                "/api/saveUser", "/api/getAllUser", "/api/getUser/{id}")
                                                .permitAll()
                                                .anyRequest().authenticated())
                                .sessionManagement(session -> session
                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

                http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

                return http.build();
        }

        @Bean
        public BCryptPasswordEncoder bCryptPasswordEncoder() {
                return new BCryptPasswordEncoder();
        }
}