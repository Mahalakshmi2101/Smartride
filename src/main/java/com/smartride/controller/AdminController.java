package com.smartride.controller;

import com.smartride.dto.AdminStatsResponse;
import com.smartride.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired private AdminService adminService;

    @GetMapping("/stats")
    public ResponseEntity<AdminStatsResponse> getStats() {
        return ResponseEntity.ok(adminService.getStats());
    }

    @GetMapping("/users")
    public ResponseEntity<List<?>> getAllUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @GetMapping("/rides")
    public ResponseEntity<List<?>> getAllRides() {
        return ResponseEntity.ok(adminService.getAllRides());
    }

    @GetMapping("/payments")
    public ResponseEntity<List<?>> getAllPayments() {
        return ResponseEntity.ok(adminService.getAllPayments());
    }

    @DeleteMapping("/users/{userId}/block")
    public ResponseEntity<String> blockUser(@PathVariable Long userId) {
        adminService.blockUser(userId);
        return ResponseEntity.ok("User blocked successfully");
    }
}