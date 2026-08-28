package com.smartride.dto;


import java.math.BigDecimal;

public class PaymentResponse {
    private Long paymentId;
    private String razorpayOrderId;
    private BigDecimal amount;
    private String status;
    private String currency;

    public PaymentResponse() {}

    public PaymentResponse(Long paymentId, String razorpayOrderId,
                           BigDecimal amount, String status, String currency) {
        this.paymentId = paymentId;
        this.razorpayOrderId = razorpayOrderId;
        this.amount = amount;
        this.status = status;
        this.currency = currency;
    }

    public Long getPaymentId() { return paymentId; }
    public String getRazorpayOrderId() { return razorpayOrderId; }
    public BigDecimal getAmount() { return amount; }
    public String getStatus() { return status; }
    public String getCurrency() { return currency; }
}