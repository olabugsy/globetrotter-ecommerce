package com.globtrotter.service;

import com.globtrotter.dto.RegisterRequest;
import com.globtrotter.model.Role;
import com.globtrotter.model.User;
import com.globtrotter.repository.RoleRepository;
import com.globtrotter.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RegistrationService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public RegistrationService(UserRepository userRepository,
                               RoleRepository roleRepository,
                               PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public String register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            return "❌ Email already in use";
        }

        Role role = roleRepository.findByName(request.getRole())
                .orElseThrow(() -> new RuntimeException("❌ Role not found: " + request.getRole()));

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(role);

        userRepository.save(user);
        System.out.println("✅ Registration successful for email: " + request.getEmail() + ", role: " + request.getRole());
        return "✅ Registration successful";
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
