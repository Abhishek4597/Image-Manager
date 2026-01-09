package com.imagemanager.service;

import com.imagemanager.entity.User;
import com.imagemanager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @EventListener(ApplicationReadyEvent.class)
    public void createDefaultUser() {
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User("admin", passwordEncoder.encode("admin"), "admin@example.com","ADMIN");
            userRepository.save(admin);
            System.out.println("Default admin user created");
        }
    }
    
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    public User save(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }
    
    public boolean userExists(String username) {
        return userRepository.existsByUsername(username);
    }
    
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }
    
    public User createUser(String username, String email, String password, String role) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("Username already exists: " + username);
        }
        
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("Email already exists: " + email);
        }
        
        if (!isValidRole(role)) {
            throw new RuntimeException("Invalid role: " + role + ". Valid roles are: VIEWER, TAGGER, UPLOADER, MODERATOR, ADMIN");
        }
        
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(role);
        user.setEnabled(true);
        user.setCreatedDate(LocalDateTime.now());
        
        return userRepository.save(user);
    }
    
    private boolean isValidRole(String role) {
        return role != null && (
            role.equals("VIEWER") || 
            role.equals("TAGGER") || 
            role.equals("UPLOADER") || 
            role.equals("MODERATOR") || 
            role.equals("ADMIN")
        );
    }
    
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    public long getUserCount() {
        return userRepository.count();
    }
    
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }
    
    public void updateUserRole(Long userId, String newRole) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
        
        if (!isValidRole(newRole)) {
            throw new RuntimeException("Invalid role: " + newRole);
        }
        
        user.setRole(newRole);
        userRepository.save(user);
    }
}