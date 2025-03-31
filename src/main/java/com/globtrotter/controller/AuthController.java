package com.globtrotter.controller;

import com.globtrotter.dto.RegisterRequest;
import com.globtrotter.model.User;
import com.globtrotter.service.RegistrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final RegistrationService registrationService;

    @Autowired
    public AuthController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody RegisterRequest request) {
        String response = registrationService.register(request);
        return response.startsWith("✅")
                ? ResponseEntity.ok(response)
                : ResponseEntity.badRequest().body(response);
    }

}
