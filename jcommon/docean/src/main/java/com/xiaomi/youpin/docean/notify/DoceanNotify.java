package com.xiaomi.youpin.docean.notify;

import com.xiaomi.youpin.docean.bo.Bean;
import com.xiaomi.youpin.docean.bo.MvcConfig;
import com.xiaomi.youpin.docean.listener.Listener;
import com.xiaomi.youpin.docean.listener.event.Event;
import com.xiaomi.youpin.docean.listener.event.EventType;
import lombok.extern.slf4j.Slf4j;

/**
 * @author goodjava@qq.com
 * @date 2022/5/8 17:14
 */
@Slf4j
public class DoceanNotify implements Listener {

    @Override
    public void onEvent(Event event) {
        if (event.getEventType().equals(EventType.addBean)) {
            String name = event.getData();
            log.info("Docean ioc:{} add bean:{}", event.getIocName(), name);
        } else if (event.getEventType().equals(EventType.initFinish)) {
            long useTime = event.getData();
            String name = event.getAttachments().getOrDefault("name", "").toString();
            log.info("Docean ioc:{} {} init finish use time:{}ms", event.getIocName(), name, useTime);
        } else if (event.getEventType().equals(EventType.putBean)) {
            Bean bean = event.getData();
            log.debug("Docean ioc put bean:{}", bean.getName());
        } else if (event.getEventType().equals(EventType.mvcBegin)) {
            MvcConfig mvcConfig = event.getData();
            log.info("Docean mvc config:{}", mvcConfig);
        } else if (event.getEventType().equals(EventType.initController)) {
            String path = event.getData();
            log.info("Docean mvc add controller path:{}", path);
        }

        switch (event.getEventType()) {
            case initBean: {
                Bean bean = event.getData();
                long useTime = (long) event.getAttachments().get("useTime");
                if (useTime > 50) {
                    log.warn("Docean ioc:{} init bean:{} use time:{}ms", event.getIocName(), bean.getName(), useTime);
                }
                break;
            }
        }


    }
}
