package com.xiaomi.youpin.dubbo.filter;

import com.xiaomi.youpin.infra.rpc.errors.BizError;
import com.xiaomi.youpin.infra.rpc.errors.GeneralCodes;
import org.apache.dubbo.rpc.Result;

/**
 * @author goodjava@qq.com
 */
public abstract class ResultUtils {


    public static int getCode(Result res) {
        int code = GeneralCodes.OK.getCode();
        if (null == res) {
            return code;
        }
        try {
            if (res.hasException()) {
                Throwable ex = res.getException();
                if (ex instanceof BizError) {
                    code = (((BizError) ex).getCode());
                } else {
                    //未知错误
                    code = (GeneralCodes.InternalError.getCode());
                }
            } else {
                if (null != res.getValue() && (res.getValue() instanceof com.xiaomi.youpin.infra.rpc.Result)) {
                    code = (((com.xiaomi.youpin.infra.rpc.Result) res.getValue()).getCode());
                } else {
                    code = (GeneralCodes.OK.getCode());
                }
            }
        } catch (Throwable ex) {
            //ignore
        }
        return code;
    }
}
