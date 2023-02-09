package run.mone.docean.plugin.sidecar.interceptor;

import com.xiaomi.data.push.common.UdsException;
import com.xiaomi.data.push.uds.po.UdsCommand;

/**
 * @author goodjava@qq.com
 * @date 2022/11/24 09:29
 */
public class ExceptionProcessor {

    public void processException(UdsCommand res) throws Throwable{
        if (res.getCode() != 0) {
            String message = res.getAttachments().getOrDefault("stackTrace", res.getMessage());
            throw new UdsException(message);
        }
    }

    public void processException(Throwable ex) throws Throwable{
        throw ex;
    }

}
