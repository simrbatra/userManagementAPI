package com.example.usermanagementapi.services;

import com.example.usermanagementapi.model.User;
import com.example.usermanagementapi.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
public class DataSeeder {
    @Bean
    CommandLineRunner initAdmin(UserRepository repo, PasswordEncoder encoder) {
        return args -> repo.findByEmail("admin@gmail.com").orElseGet(() -> {
            User admin = new User();
            admin.setName("Admin");
            admin.setEmail("admin@gmail.com");
            admin.setPassword(encoder.encode("admin123")); // BCrypt
            admin.setRoles(Set.of("ROLE_ADMIN"));
            return repo.save(admin);
        });
    }
}