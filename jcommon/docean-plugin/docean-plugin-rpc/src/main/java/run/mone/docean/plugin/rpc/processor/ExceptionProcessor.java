package run.mone.docean.plugin.rpc.processor;

import com.xiaomi.data.push.rpc.exception.RemotingException;
import com.xiaomi.data.push.rpc.protocol.RemotingCommand;

/**
 * @author goodjava@qq.com
 * @date 2022/11/24 09:29
 */
public class ExceptionProcessor {

    public void processException(RemotingCommand res) throws Throwable{
        if (res.getExtField("code").equals("500")) {
            String message = res.getExtFields().getOrDefault("stackTrace", res.getMessage());
            throw new RemotingException(message);
        }
    }

    public void processException(Throwable ex) throws Throwable{
        throw ex;
    }

}
