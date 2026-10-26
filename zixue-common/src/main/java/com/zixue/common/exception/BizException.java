package com.zixue.common.exception;

import com.zixue.common.enums.ErrorCode;
import lombok.Getter;

/**
 * 业务异常。
 */
@Getter
public class BizException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final int code;

    public BizException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }

    public BizException(int code, String message) {
        super(message);
        this.code = code;
    }
}
