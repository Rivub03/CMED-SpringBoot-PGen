package com.rivu.pgen.prescription_generator.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(requests -> requests
                        // Grant access to all static resources
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()
                        // Grant access to the login page
                        .requestMatchers("/login").permitAll()
                        // All other requests require authentication
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login") // Use the custom login path
                        .permitAll()
                        // **THE FIX:** Redirect to the prescription list page after a successful login
                        .defaultSuccessUrl("/prescriptions", true)
                )
                .logout(logout -> logout
                        .permitAll()
                );
        return http.build();
    }
}