package com.mycomp.payments.paypal.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class PaypalOrderRes {

    private String id;
    private String status;

    @JsonProperty("payment_source")
    private PaymentSource paymentSource;

    private List<PaypalLink> links;

    // Getters & Setters
}
