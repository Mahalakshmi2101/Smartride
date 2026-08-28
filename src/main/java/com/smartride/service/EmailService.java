package com.smartride.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired private JavaMailSender mailSender;

    public void sendEmail(String toEmail, String subject, String body) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(toEmail);
            message.setSubject(subject);
            message.setText(body);
            message.setFrom("your-email@gmail.com");
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Email send failed: " + e.getMessage());
        }
    }

    public void sendBookingConfirmation(String toEmail, String driverName,
                                        String source, String destination) {
        String subject = "SmartRide — Booking Confirmed!";
        String body = "Your ride has been confirmed.\n\n" +
            "Driver: " + driverName + "\n" +
            "From: " + source + "\n" +
            "To: " + destination + "\n\n" +
            "Thank you for choosing SmartRide!";
        sendEmail(toEmail, subject, body);
    }

    public void sendRideCancellation(String toEmail, String source, String destination) {
        String subject = "SmartRide — Ride Cancelled";
        String body = "Your ride from " + source + " to " + destination +
            " has been cancelled.\n\nWe apologize for the inconvenience.";
        sendEmail(toEmail, subject, body);
    }
}