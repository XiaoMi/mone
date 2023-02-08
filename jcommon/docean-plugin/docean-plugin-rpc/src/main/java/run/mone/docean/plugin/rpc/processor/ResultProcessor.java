package run.mone.docean.plugin.rpc.processor;

import com.xiaomi.data.push.rpc.protocol.RemotingCommand;
import com.xiaomi.data.push.uds.codes.CodeType;
import com.xiaomi.data.push.uds.codes.CodesFactory;
import com.xiaomi.data.push.uds.codes.ICodes;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;

/**
 * @author goodjava@qq.com
 * @date 2022/11/28 15:27
 */
@Slf4j
public class ResultProcessor {

    public Object processResult(RemotingCommand req, RemotingCommand res, Method method) {
        if (method.getReturnType().equals(void.class)) {
            return null;
        }
        //返回结果就是空
        if (res.getExtFields().getOrDefault("res_is_null", "false").equals("true")) {
            return null;
        }
        byte[] data = res.getBody();
        ICodes codes = CodesFactory.getCodes(CodeType.PROTOSTUFF);
        Object result = codes.decode(data,method.getReturnType());
        log.debug("call sidecar:{} receive:{}", method.getName(), result);
        return result;
    }

}
