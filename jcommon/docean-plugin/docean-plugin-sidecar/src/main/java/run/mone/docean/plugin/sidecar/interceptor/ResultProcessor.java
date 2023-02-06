package run.mone.docean.plugin.sidecar.interceptor;

import com.xiaomi.data.push.uds.po.UdsCommand;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

/**
 * @author goodjava@qq.com
 * @date 2022/11/28 15:27
 */
@Slf4j
public class ResultProcessor {

    public Object processResult(UdsCommand req, UdsCommand res, Method method) {
        if (method.getReturnType().equals(void.class)) {
            return null;
        }
        //返回结果就是空
        if (res.getAtt("res_is_null", "false").equals("true")) {
            return null;
        }
        Object result = res.getData(method.getReturnType());
        log.debug("call sidecar:{} receive:{}", method.getName(), result);
        return result;
    }

}
