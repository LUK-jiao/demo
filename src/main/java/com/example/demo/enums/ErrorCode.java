package com.example.demo.enums;

public enum ErrorCode {

    ORDER_NOT_FOUND("ORDER_404", "订单不存在"),
    ORDER_FORBIDDEN("ORDER_403", "无权操作该订单"),
    ORDER_STATUS_INVALID("ORDER_409", "订单状态不允许修改"),
    PARAM_INVALID("PARAM_400", "参数不合法");

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
