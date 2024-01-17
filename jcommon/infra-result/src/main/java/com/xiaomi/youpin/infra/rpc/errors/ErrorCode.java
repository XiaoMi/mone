package com.xiaomi.youpin.infra.rpc.errors;

import com.xiaomi.youpin.infra.rpc.Result;
import com.xiaomi.youpin.infra.rpc.errors.ErrorScope;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;
import javax.lang.model.type.ErrorType;

/**
 * Created by daxiong on 2018/12/20.
 */
public class ErrorCode {
    /**
     * 业务必须用本包的静态方法创建错误码
     * 错误码在同一个项目中，必须静态声明到同一个文件里
     * 公用错误码是预定义的，在GeneralCodes里
     *
     * @param code
     */
    ErrorCode(int code) {
        this.code = code;
    }

    /**
     * 业务码统一以400开头，用于快速区分可用性
     */
    private static final int ERROR_CODE_PREFIX = 400;
    private static Set<Integer> allCodes = new HashSet<>();

    @Getter
    private int code;

    /**
     * 创建一个业务错误码
     * 形如 [400][110][258]的形式，前三位为400，中间三位是业务范围，末尾三位是内部码
     *
     * @param errorScope   错误范围，一般与服务一一对应，如果考虑到服务会拆分，可以一个服务占用多个范围
     * @param internalCode 内部码，错误范围内保证不重复
     * @return
     */
    public static ErrorCode createOnce(ErrorScope errorScope, int internalCode) {
        if (internalCode < 0 || internalCode >= 1000) {
            throw new IllegalArgumentException("Bad internal code range. " + internalCode);
        }
        ErrorCode code = new ErrorCode(Integer.valueOf(String.format("%03d%03d%03d", ERROR_CODE_PREFIX, errorScope.getScopeId(), internalCode)));
        if (allCodes.contains(code.getCode())) {
            throw new IllegalArgumentException("Duplicated error code defination.");
        }
        allCodes.add(code.getCode());
        return code;
    }

    /**
     * fromResult gets code as ErrorCode from Result.
     * code in Result is always defined in advance.
     */
    public static ErrorCode fromResult(Result result) {
        return result == null ? null : new ErrorCode(result.getCode());
    }
}
