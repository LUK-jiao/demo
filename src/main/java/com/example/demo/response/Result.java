package com.example.demo.response;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

@Data
public class Result {

    private boolean success;
    private String message;

    public static Result success() {
        Result result = new Result();
        result.setSuccess(true);
        result.setMessage("Operation successful");
        return result;
    }

    public static Result failure(String message) {
        Result result = new Result();
        result.setSuccess(false);
        result.setMessage(message);
        return result;
    }
}
