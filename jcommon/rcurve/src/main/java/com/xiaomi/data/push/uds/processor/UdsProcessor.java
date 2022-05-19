package com.xiaomi.data.push.uds.processor;

import com.xiaomi.data.push.uds.po.UdsCommand;

/**
 * @author goodjava@qq.com
 */
public interface UdsProcessor {


    void processRequest(UdsCommand request);


    default String cmd() {
        return "";
    }


}
