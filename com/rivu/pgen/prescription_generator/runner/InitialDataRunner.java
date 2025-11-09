package com.rivu.pgen.prescription_generator.runner;

import com.rivu.pgen.prescription_generator.entity.User;
import com.rivu.pgen.prescription_generator.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class InitialDataRunner implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public InitialDataRunner(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.findByUserName("doctor").isEmpty()) { // Note: Changed to findByUserName
            User doctor = new User();
            doctor.setUserName("doctor");
            doctor.setPassword(passwordEncoder.encode("password"));
            doctor.setName("Dr. John Doe");
            doctor.setAge(45);
            doctor.setGender("Male");

            // 👇 FIX: Set the role
            doctor.setRole("ROLE_DOCTOR");

            userRepository.save(doctor);
            System.out.println("Default user 'doctor' created with password 'password'");
        }
    }
}
