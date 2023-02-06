package com.xiaomi.data.push.uds.classloader;

import com.google.common.base.Throwables;
import com.xiaomi.data.push.uds.context.CallContext;
import com.xiaomi.data.push.uds.context.ContextHolder;
import com.xiaomi.data.push.uds.po.UdsCommand;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author goodjava@qq.com
 * @date 2022/11/26 15:32
 */
@Slf4j
public class ClassLoaderExecute {


    private final BiFunction<Throwable, UdsCommand, Throwable> throwableFunction;


    public ClassLoaderExecute(BiFunction<Throwable, UdsCommand, Throwable> throwableFunction) {
        this.throwableFunction = throwableFunction;
    }

    public Object execute(Supplier supplier, Function<String, ClassLoader> classLoaderFunction, UdsCommand response, UdsCommand request) {
        response.putAtt("dubbo_mesh", request.getAtt("dubbo_mesh", "false"));
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        CallContext ctx = new CallContext();
        ctx.setAttrs(request.getAttachments());
        ContextHolder.getContext().set(ctx);
        try {
            if (null != classLoaderFunction) {
                Thread.currentThread().setContextClassLoader(classLoaderFunction.apply(""));
            }
            Object res = supplier.get();

            if (res instanceof AttResult) {
                AttResult attResult = (AttResult) res;
                res = attResult.getRes();
                attResult.getAttachments().forEach((k,v)-> response.putAtt(k,v));
            }

            Optional optional = Optional.ofNullable(res);
            if (optional.isPresent()) {
                response.putAtt("res_return_type", res.getClass().getName());
            } else {
                response.putAtt("res_is_null", "true");
            }
            response.setData(res);
            return res;
        } catch (Throwable ex) {
            log.error("invoke method error:" + ex.getMessage(), ex);
            response.setCode(500);
            response.setMessage(ex.getMessage());
            response.putAtt("exceptionType", ex.getClass().getName());
            response.putAtt("stackTrace", Throwables.getStackTraceAsString(ex));
            response.setData(throwableFunction.apply(ex, response));
            return null;
        } finally {
            if (null != classLoaderFunction) {
                Thread.currentThread().setContextClassLoader(cl);
            }
            ContextHolder.getContext().close();
        }
    }

}
