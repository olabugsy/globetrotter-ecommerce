package com.globtrotter.controller;

import com.globtrotter.model.User;
import com.globtrotter.service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final RegistrationService registrationService;

    @Autowired
    public AdminController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = registrationService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/dashboard")
    public ResponseEntity<String> dashboard() {
        return ResponseEntity.ok(" Welcome to the Admin Dashboard!");
    }


}
