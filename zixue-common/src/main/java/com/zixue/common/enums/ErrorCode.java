package com.zixue.common.enums;

/**
 * 业务错误码。
 */
public enum ErrorCode {

    SUCCESS(0, "成功"),
    SYSTEM_ERROR(500, "系统繁忙，请稍后重试"),
    PARAM_ERROR(400, "参数错误"),
    PROGRESS_NOT_FOUND(404, "学习进度不存在"),
    ANTI_CHEAT_REJECTED(429, "上报被拒绝"),
    ;

    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
