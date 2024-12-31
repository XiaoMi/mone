package run.mone.local.docean.service;

import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.anno.Service;
import lombok.extern.slf4j.Slf4j;
import run.mone.local.docean.enums.ImEnum;
import run.mone.local.docean.service.api.ImStrategy;

@Service
@Slf4j
public class ImContext {

    public boolean sendMessage(String message, String toId, ImEnum imType) {
        ImStrategy imStrategy = null;
        if (imType == ImEnum.FEISHU) {
            imStrategy = Ioc.ins().getBean("feiShu");
        } else if (imType == ImEnum.WEIXIN) {
            imStrategy = Ioc.ins().getBean("weiXin");
        }
        return imStrategy.sendMessage(message, toId);
    }

    public Object replyMessage(String message, String toId, ImEnum imType) {
        log.info("replayMessage begin,imType:{}",imType.getValue());
        ImStrategy imStrategy = null;
        if (imType == ImEnum.FEISHU) {
            imStrategy = Ioc.ins().getBean("feiShu");
        } else if (imType == ImEnum.WEIXIN) {
            imStrategy = Ioc.ins().getBean("weiXin");
        }
        return imStrategy.replyMessage(message, toId);
    }
}
