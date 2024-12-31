package run.mone.ultraman.listener;

import run.mone.ultraman.listener.bo.AthenaMessage;


/**
 * @author goodjava@qq.com
 * @date 2023/7/14 14:52
 */
public interface AthenaMessageListener {


    void handleMessage(AthenaMessage message);


}
