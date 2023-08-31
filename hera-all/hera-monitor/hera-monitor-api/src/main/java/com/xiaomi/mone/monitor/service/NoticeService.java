package com.xiaomi.mone.monitor.service;

import com.xiaomi.mone.monitor.service.bo.CapacityAdjustNoticeParam;

/**
 * @author gaoxihui
 * @date 2022/12/1 4:20 下午
 */
public interface NoticeService {

    Boolean capacityAdjustNotice(CapacityAdjustNoticeParam param);

}
