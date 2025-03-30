package com.globtrotter.service;

import com.globtrotter.dto.RegisterRequest;
import com.globtrotter.model.Role;
import com.globtrotter.model.User;
import com.globtrotter.repository.RoleRepository;
import com.globtrotter.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

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

        Optional<Role> customerRoleOpt = roleRepository.findByName("CUSTOMER");
        if (customerRoleOpt.isEmpty()) {
            return "❌ 'CUSTOMER' role not found";
        }

        Role customerRole = customerRoleOpt.get();

        User user = new User();
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(customerRole);

        userRepository.save(user);
        return "✅ Registration successful";
    }
}
