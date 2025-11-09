package com.rivu.pgen.prescription_generator.service;

import com.rivu.pgen.prescription_generator.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class JpaUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public JpaUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Find the User entity by username (case-sensitive)
        return userRepository.findByUserName(username)
                .map(CustomUserDetails::new) // Map the User entity to our custom UserDetails wrapper
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }
}
