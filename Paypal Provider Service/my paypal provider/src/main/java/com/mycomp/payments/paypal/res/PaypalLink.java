package com.mycomp.payments.paypal.res;

import lombok.Data;

@Data
public class PaypalLink {
    private String href;
    private String rel;
    private String method;
}