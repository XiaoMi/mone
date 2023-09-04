package com.xiaomi.mone.app.exception;

import com.xiaomi.mone.app.common.Result;
import com.xiaomi.mone.app.enums.BizCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/10/29 13:27
 */
@Slf4j
@RestControllerAdvice(basePackages = "com.xiaomi.mone.app.controller")
public class AppExceptionControllerAdvice {

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public Result handleValidException(MethodArgumentNotValidException exception) {
        log.info("数据类型校验出现异常:{},异常类型{}", exception.getMessage(), exception.getClass());
        Map<String, String> errorMap = new HashMap<String, String>();
        exception.getBindingResult().getFieldErrors().forEach(fieldError -> {
            errorMap.put(fieldError.getField(), fieldError.getDefaultMessage());
        });
        return Result.error(BizCodeEnum.VALID_EXCEPTION.getCode(), BizCodeEnum.VALID_EXCEPTION.getMsg(), errorMap);
    }

    @ExceptionHandler(value = MissingServletRequestParameterException.class)
    public Result handleParameterException(MissingServletRequestParameterException exception) {
        log.info("参数校验出现异常{}，异常类型{}", exception.getMessage(), exception.getClass());
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put(exception.getParameterName(), exception.getParameterType());
        return Result.error(BizCodeEnum.VALID_EXCEPTION.getCode(), exception.getMessage(), errorMap);
    }

    @ExceptionHandler(value = Exception.class)
    public Result handleException(Exception e) {
        log.info("服务器异常", e);
        return Result.error(e.getMessage());
    }
}
