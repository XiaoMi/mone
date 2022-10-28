//package com.xiaomi.youpin.gwdash.interceptor;
//
//import com.fasterxml.jackson.databind.JsonMappingException;
//import com.fasterxml.jackson.databind.exc.InvalidFormatException;
//import com.xiaomi.youpin.gwdash.controller.ScepterController;
//import com.xiaomi.youpin.infra.rpc.Result;
//import com.xiaomi.youpin.scepter.api.constants.ErrorsCode;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.ibatis.binding.BindingException;
//import org.springframework.http.converter.HttpMessageNotReadableException;
//import org.springframework.web.bind.MethodArgumentNotValidException;
//import org.springframework.web.bind.annotation.ControllerAdvice;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.ResponseBody;
//
//import javax.validation.ConstraintViolation;
//import javax.validation.ConstraintViolationException;
//import javax.validation.ValidationException;
//import java.util.HashSet;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Objects;
//
///**
// * @author gaoyibo
// */
//@ControllerAdvice(basePackageClasses = ScepterController.class)
//@Slf4j
//@ResponseBody
//public class ScepterExceptionHandler {
//
//    @ExceptionHandler(BindingException.class)
//    public Result<String> methodArguments(BindingException e) {
//        return Result.fail(ErrorsCode.PARAM_ERROR, "");
//    }
//
//    @ExceptionHandler(ValidationException.class)
//    public Result<String> methodArguments(ValidationException e) {
//        return Result.fail(ErrorsCode.PARAM_ERROR, e.getCause().getMessage());
//    }
//
//    /**
//     * GET 方法
//     */
//    @ExceptionHandler(ConstraintViolationException.class)
//    public Result<String> handleConstrainViolationHandler(ConstraintViolationException e) {
//        String msg = "";
//        HashSet<ConstraintViolation<?>> set = (HashSet<ConstraintViolation<?>>) e.getConstraintViolations();
//        Iterator<ConstraintViolation<?>> iterator = set.iterator();
//        if (iterator.hasNext()) {
//            ConstraintViolation<?> next = iterator.next();
//            // 只取一个异常信息返回
//            msg = next.getMessageTemplate();
//        }
//
//        //返回自定义信息格式
//        return Result.fail(ErrorsCode.PARAM_ERROR, msg);
//    }
//
//    /**
//     * POST 方法
//     */
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public Result<String> methodArguments(MethodArgumentNotValidException e) {
//        return Result.fail(ErrorsCode.PARAM_ERROR, Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage());
//    }
//
//    @ExceptionHandler(HttpMessageNotReadableException.class)
//    public Result<String> messageNotReadable(HttpMessageNotReadableException exception){
//        InvalidFormatException formatException =
//                (InvalidFormatException)exception.getCause();
//
//        List<JsonMappingException.Reference> e = formatException.getPath();
//        String field = "";
//        for (JsonMappingException.Reference reference :e){
//            //这里获得了类型匹配出错的属性的属性名
//            field = reference.getFieldName();
//        }
//
//        return Result.fail(ErrorsCode.PARAM_ERROR, field + "参数类型不匹配");
//    }
//
//    @ExceptionHandler(Exception.class)
//    public Result<String> methodArguments(Exception e) {
//        return Result.fail(ErrorsCode.PARAM_ERROR, e.getMessage());
//    }
//
//}
