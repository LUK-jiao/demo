package com.example.demo.exceptions;

import com.example.demo.enums.ErrorCode;

public class BusinessException extends RuntimeException {

    private final String errorCode;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode.getCode();
    }

    public BusinessException(String message) {
        super(message);
        this.errorCode = null;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
