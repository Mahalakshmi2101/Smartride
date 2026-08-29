package com.smartride.controller;

import com.smartride.dto.LoginRequest;
import com.smartride.dto.RegisterRequest;
import com.smartride.model.User;
import com.smartride.security.jwt.JwtUtils;
import com.smartride.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody RegisterRequest request) {
        Map<String, Object> response = new HashMap<>();
        try {
            User user = userService.registerNewUser(
                request.getName(),
                request.getEmail(),
                request.getPassword(),
                request.getRole()
            );

            // generate token immediately so frontend can log in right after register
            String token = jwtUtils.generateToken(user);

            response.put("message", "Registration successful");
            response.put("token",   token);
            response.put("userId",  user.getId());
            response.put("name",    user.getName());
            response.put("email",   user.getEmail());
            response.put("role",    user.getRoles());
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            response.put("message", e.getMessage());
            return ResponseEntity.status(400).body(response);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    request.getEmail(),
                    request.getPassword()
                )
            );

            User user = (User) authentication.getPrincipal();
            String token = jwtUtils.generateToken(user);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "Login successful");
            response.put("token",   token);
            response.put("userId",  user.getId());
            response.put("name",    user.getName());
            response.put("email",   user.getEmail());
            response.put("role",    user.getRoles());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Invalid email or password");
            return ResponseEntity.status(401).body(response);
        }
    }

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() ||
                authentication.getPrincipal().equals("anonymousUser")) {
            Map<String, Object> response = new HashMap<>();
            response.put("error", "Not authenticated");
            return ResponseEntity.status(401).body(response);
        }

        User user = (User) authentication.getPrincipal();

        Map<String, Object> response = new HashMap<>();
        response.put("userId", user.getId());
        response.put("name",   user.getName());
        response.put("email",  user.getEmail());
        response.put("role",   user.getRoles());
        return ResponseEntity.ok(response);
    }
}