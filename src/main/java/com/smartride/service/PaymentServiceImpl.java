package com.smartride.service;

import com.smartride.dto.PaymentRequest;
import com.smartride.dto.PaymentResponse;
import com.smartride.exception.PaymentFailedException;
import com.smartride.exception.ResourceNotFoundException;
import com.smartride.model.Fare;
import com.smartride.model.Payment;
import com.smartride.model.entity.Ride;
import com.smartride.model.User;
import com.smartride.repository.FareRepository;
import com.smartride.repository.PaymentRepository;
import com.smartride.repository.RideRepository;
import com.smartride.repository.UserRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Value("${razorpay.key.id}")
    private String keyId;

    @Value("${razorpay.key.secret}")
    private String keySecret;

    private final PaymentRepository paymentRepository;
    private final FareRepository fareRepository;
    private final RideRepository rideRepository;
    private final UserRepository userRepository;

    public PaymentServiceImpl(PaymentRepository paymentRepository,
                               FareRepository fareRepository,
                               RideRepository rideRepository,
                               UserRepository userRepository) {
        this.paymentRepository = paymentRepository;
        this.fareRepository = fareRepository;
        this.rideRepository = rideRepository;
        this.userRepository = userRepository;
    }

    @Override
    public PaymentResponse createOrder(PaymentRequest request) {
        Ride ride = rideRepository.findById(request.getRideId())
            .orElseThrow(() -> new ResourceNotFoundException("Ride not found"));

        User passenger = userRepository.findById(request.getPassengerId())
            .orElseThrow(() -> new ResourceNotFoundException("Passenger not found"));

        Fare fare = fareRepository.findByRideId(request.getRideId())
            .orElseThrow(() -> new ResourceNotFoundException("Fare not calculated for this ride yet"));

        try {
            RazorpayClient client = new RazorpayClient(keyId, keySecret);

            JSONObject orderRequest = new JSONObject();
            // Razorpay takes amount in paise (1 INR = 100 paise)
            orderRequest.put("amount", fare.getPerPassengerFare().multiply(BigDecimal.valueOf(100)).intValue());
            orderRequest.put("currency", "INR");
            orderRequest.put("receipt", "receipt_ride_" + ride.getId());

            Order order = client.orders.create(orderRequest);

            // Save payment record as PENDING
            Payment payment = new Payment(ride, passenger, fare.getPerPassengerFare());
            payment.setRazorpayOrderId(order.get("id"));
            paymentRepository.save(payment);

            return new PaymentResponse(
                payment.getId(),
                order.get("id"),
                fare.getPerPassengerFare(),
                "PENDING",
                "INR"
            );

        } catch (RazorpayException e) {
            throw new PaymentFailedException("Failed to create Razorpay order: " + e.getMessage());
        }
    }

    @Override
    public PaymentResponse verifyAndComplete(String razorpayOrderId, String razorpayPaymentId) {
        Payment payment = paymentRepository.findAll().stream()
            .filter(p -> razorpayOrderId.equals(p.getRazorpayOrderId()))
            .findFirst()
            .orElseThrow(() -> new ResourceNotFoundException("Payment not found for order: " + razorpayOrderId));

        payment.setRazorpayPaymentId(razorpayPaymentId);
        payment.setStatus(Payment.PaymentStatus.SUCCESS);
        payment.setCompletedAt(LocalDateTime.now());
        paymentRepository.save(payment);

        return new PaymentResponse(
            payment.getId(),
            payment.getRazorpayOrderId(),
            payment.getAmount(),
            "SUCCESS",
            "INR"
        );
    }

    @Override
    public List<PaymentResponse> getPaymentHistory(Long passengerId) {
        return paymentRepository.findByPassengerId(passengerId).stream()
            .map(p -> new PaymentResponse(
                p.getId(),
                p.getRazorpayOrderId(),
                p.getAmount(),
                p.getStatus().name(),
                "INR"
            ))
            .collect(Collectors.toList());
    }
}