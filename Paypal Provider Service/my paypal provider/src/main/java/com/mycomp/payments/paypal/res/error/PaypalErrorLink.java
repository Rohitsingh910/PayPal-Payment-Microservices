package com.mycomp.payments.paypal.res.error;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PaypalErrorLink {

    // PayPal API normally sends "href" for link URL
    private String href;

    private String rel;

    @JsonProperty("encType")
    private String encType;
}