package com.mycomp.payments.util;

import com.mycomp.payments.paypal.res.error.PaypalErrorDetail;
import com.mycomp.payments.paypal.res.error.PaypalErrorResponse;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PaypalOrderUtil {
	private PaypalOrderUtil() {}

	public static String getPaypalErrorSummary(PaypalErrorResponse res) {
		log.info("Generating PayPal error summary from response: {}", res);
		if (res == null) {
			return "Unknown PayPal error";
		}

		StringBuilder summary = new StringBuilder();

		// Append if not null
		appendIfPresent(summary, res.getName());
		appendIfPresent(summary, res.getMessage());
		appendIfPresent(summary, res.getError());
		appendIfPresent(summary, res.getErrorDescription());

		if (res.getDetails() != null && !res.getDetails().isEmpty()) {
			PaypalErrorDetail detail = res.getDetails().get(0);
			if (detail != null) {
				appendIfPresent(summary, detail.getField());
				appendIfPresent(summary, detail.getIssue());
				appendIfPresent(summary, detail.getDescription());
			}
		}

		log.info("Generated PayPal error summary: {}", summary.toString());
		return summary.length() > 0 ? summary.toString() : "Unknown PayPal error";
	}

	private static void appendIfPresent(StringBuilder sb, String value) {
		if (value != null && !value.isBlank()) {
			if (sb.length() > 0) sb.append(" | ");
			sb.append(value.trim());
		}
	}


}
