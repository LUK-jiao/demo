package com.example.demo.exceptions;

import com.example.demo.enums.ErrorCode;

public class BusinessException extends RuntimeException {

    private final String errorCode;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode.getCode();
    }

    public String getErrorCode() {
        return errorCode;
    }
}
