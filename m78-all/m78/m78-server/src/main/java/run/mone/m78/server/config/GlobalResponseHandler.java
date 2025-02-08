package run.mone.m78.server.config;

import com.google.common.base.Throwables;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import run.mone.m78.service.exceptions.GenericServiceException;
import run.mone.m78.service.exceptions.InvalidArgumentException;
import com.xiaomi.youpin.infra.rpc.Result;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static run.mone.m78.service.exceptions.ExCodes.*;


/**
 * @author HawickMason@xiaomi.com
 * @date 1/10/24 10:58 AM
 */
@ControllerAdvice
@Slf4j
public class GlobalResponseHandler implements ResponseBodyAdvice {

    private Set<String> ignoreBeforeBodyWrite = Sets.newHashSet("/api/v1/audio/textToAudio");

    @ExceptionHandler(Throwable.class)
    @ResponseBody
    Result handleUnexpectedException(Throwable e) {
        Throwable rootCause = Throwables.getRootCause(e);
        log.error("Unexpected internal error, nested exception is  ", e);
        return Result.fail(STATUS_INTERNAL_ERROR, rootCause + ", please contact mione lark for help");
    }

    @ExceptionHandler(GenericServiceException.class)
    @ResponseBody
    Result handleDomainBusinessException(GenericServiceException e) {
        log.error("DomainBusinessException occur, nested exception is  ", e);
        String message = e.getMessage();
        return Result.fail(STATUS_INTERNAL_ERROR, message);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result handleValidationExceptions(MethodArgumentNotValidException e) {
        log.error("MethodArgumentNotValidException occur, nested exception is  ", e);
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        log.error("MethodArgumentNotValidException detail, errors:{}", errors);
        InvalidArgumentException invalidArgumentException = new InvalidArgumentException("请求参数错误:" + StringUtils.join(errors));
        return Result.fail(STATUS_BAD_REQUEST, invalidArgumentException.getMessage());
    }

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        if (true) {
            return body;
        }
        if (body instanceof Result) {
            Result res = (Result) body;
            // FIXME: 以后有通一修改响应内容的时候再说
            return res;
        } else {
            String path = ((ServletServerHttpRequest) request).getServletRequest().getRequestURI();
            if (ignoreBeforeBodyWrite.contains(path)){
               return body;
            }
            log.error("Unexpected internal error, not valid response: {}", body);
            return Result.fail(STATUS_INTERNAL_ERROR, "please contact mione lark for help, respBody:" + body);
        }
    }
}

