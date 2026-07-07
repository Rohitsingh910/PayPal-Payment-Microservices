package com.mycomp.validation.processing;

import lombok.Data;

@Data
public class ProcessingErrorResponse {
    private String errorCode;
    private String errorMessage;
}
