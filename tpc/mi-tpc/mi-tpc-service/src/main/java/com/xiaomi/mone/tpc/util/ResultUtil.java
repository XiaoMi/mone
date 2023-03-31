package com.xiaomi.mone.tpc.util;

import com.xiaomi.mone.tpc.common.vo.ResultVo;
import com.xiaomi.youpin.infra.rpc.Result;
import com.xiaomi.youpin.infra.rpc.errors.ErrorCode;
import com.xiaomi.youpin.infra.rpc.errors.ErrorScope;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 返回对象转换
 */
public class ResultUtil {

    private static final Map<Integer, ErrorCode> codeMap = new ConcurrentHashMap<>();
    private static final ErrorScope scope = ErrorScope.createOnce(0);

    public static <T> Result<T> build(ResultVo<T> resultVo) {
        if (resultVo.success()) {
            return Result.success(resultVo.getData());
        }
        ErrorCode errorCode = codeMap.get(resultVo.getCode());
        if (errorCode == null) {
            synchronized(codeMap) {
                errorCode = codeMap.get(resultVo.getCode());
                if (errorCode == null) {
                    errorCode = ErrorCode.createOnce(scope, resultVo.getCode());
                    codeMap.put(resultVo.getCode(), errorCode);
                }
            }
        }
        return Result.fail(errorCode, resultVo.getMessage());
    }

    public static <T> Result<T> build(Integer code ,String msg) {
        ErrorCode errorCode = codeMap.get(code);
        if (errorCode == null) {
            synchronized(codeMap) {
                errorCode = codeMap.get(code);
                if (errorCode == null) {
                    errorCode = ErrorCode.createOnce(scope, code);
                    codeMap.put(code, errorCode);
                }
            }
        }
        return Result.fail(errorCode, msg);
    }
}
