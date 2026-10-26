package com.zixue.common.exception;

import com.zixue.common.enums.ErrorCode;
import com.zixue.common.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理：将业务异常 / 参数校验异常 / 未知异常统一转换为 {@link Result}。
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BizException.class)
    public Result<Void> handleBizException(BizException e) {
        return Result.fail(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> fieldError.getField() + " " + fieldError.getDefaultMessage())
                .findFirst()
                .orElse(ErrorCode.PARAM_ERROR.getMessage());
        return Result.fail(ErrorCode.PARAM_ERROR.getCode(), message);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Result<Void> handleNotReadableException(HttpMessageNotReadableException e) {
        return Result.fail(ErrorCode.PARAM_ERROR.getCode(), "请求体格式错误");
    }

    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        log.error("系统异常", e);
        return Result.fail(ErrorCode.SYSTEM_ERROR);
    }
}
