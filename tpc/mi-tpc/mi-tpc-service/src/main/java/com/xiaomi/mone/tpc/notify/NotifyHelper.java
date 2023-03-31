package com.xiaomi.mone.tpc.notify;

import com.xiaomi.mone.tpc.dao.entity.ApplyEntity;
import com.xiaomi.mone.tpc.dao.entity.NodeUserRelEntity;
import lombok.extern.slf4j.Slf4j;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/4/6 17:39
 */
@Slf4j
public abstract class NotifyHelper {


    public abstract void sendApply(NodeUserRelEntity relEntity, String nodeName, ApplyEntity applyEntity);

    public abstract void sendApplyResult(ApplyEntity applyEntity);


}
