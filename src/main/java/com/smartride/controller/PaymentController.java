package com.smartride.controller;

import com.smartride.dto.PaymentRequest;
import com.smartride.dto.PaymentResponse;
import com.smartride.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    // Create Razorpay order (passenger initiates payment)
    @PostMapping("/create-order")
    public ResponseEntity<PaymentResponse> createOrder(@Valid @RequestBody PaymentRequest request) {
        return ResponseEntity.ok(paymentService.createOrder(request));
    }

    // Called after Razorpay payment success on frontend
    @PostMapping("/verify")
    public ResponseEntity<PaymentResponse> verifyPayment(
        @RequestParam String razorpayOrderId,
        @RequestParam String razorpayPaymentId) {
        return ResponseEntity.ok(paymentService.verifyAndComplete(razorpayOrderId, razorpayPaymentId));
    }

    // Transaction history for a passenger
    @GetMapping("/history/{passengerId}")
    public ResponseEntity<List<PaymentResponse>> getHistory(@PathVariable Long passengerId) {
        return ResponseEntity.ok(paymentService.getPaymentHistory(passengerId));
    }
}