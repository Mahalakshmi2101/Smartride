package com.smartride.service;

import com.smartride.dto.PaymentRequest;
import com.smartride.dto.PaymentResponse;
import java.util.List;

public interface PaymentService {
    PaymentResponse createOrder(PaymentRequest request);
    PaymentResponse verifyAndComplete(String razorpayOrderId, String razorpayPaymentId);
    List<PaymentResponse> getPaymentHistory(Long passengerId);
}