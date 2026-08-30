package com.smartride.controller;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.smartride.model.entity.Booking;
import com.smartride.repository.BookingRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.HexFormat;
import java.util.Map;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Value("${razorpay.key.id}")
    private String keyId;

    @Value("${razorpay.key.secret}")
    private String keySecret;

    private final BookingRepository bookingRepository;

    public PaymentController(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    // Step 1: Frontend calls this to create a Razorpay order
    @PostMapping("/create-order")
    public ResponseEntity<?> createOrder(@RequestBody Map<String, Object> body) {
        try {
            Long bookingId = Long.valueOf(body.get("bookingId").toString());
            Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

            double amountPaise = booking.getNumberOfSeats() * booking.getRide().getPricePerSeat() * 100;// Razorpay needs paise

            RazorpayClient client = new RazorpayClient(keyId, keySecret);
            JSONObject orderReq = new JSONObject();
            orderReq.put("amount", amountPaise);
            orderReq.put("currency", "INR");
            orderReq.put("receipt", "booking_" + bookingId);

            Order order = client.orders.create(orderReq);

            return ResponseEntity.ok(Map.of(
                "orderId", order.get("id"),
                "amount",  amountPaise,
                "currency", "INR",
                "keyId",   keyId
            ));

        } catch (RazorpayException e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Razorpay order creation failed: " + e.getMessage()));
        }
    }

    // Step 2: Frontend calls this after payment popup closes successfully
    @PostMapping("/verify")
    public ResponseEntity<?> verifyPayment(@RequestBody Map<String, String> body,
                                            Authentication auth) {
        String razorpayOrderId   = body.get("razorpay_order_id");
        String razorpayPaymentId = body.get("razorpay_payment_id");
        String razorpaySignature = body.get("razorpay_signature");
        String bookingId         = body.get("bookingId");

        String payload = razorpayOrderId + "|" + razorpayPaymentId;

        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(keySecret.getBytes(), "HmacSHA256"));
            String computed = HexFormat.of().formatHex(mac.doFinal(payload.getBytes()));

            if (!computed.equals(razorpaySignature)) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Signature mismatch — payment not verified"));
            }

            // Mark booking as PAID
            Booking booking = bookingRepository.findById(Long.valueOf(bookingId))
                .orElseThrow(() -> new RuntimeException("Booking not found"));
            booking.setStatus("PAID");
            bookingRepository.save(booking);

            return ResponseEntity.ok(Map.of("message", "Payment verified", "bookingId", bookingId));

        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(Map.of("error", "Verification error: " + e.getMessage()));
        }
    }
}