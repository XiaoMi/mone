package com.xiaomi.mone.tpc.apply;

import com.xiaomi.mone.tpc.dao.entity.ApplyEntity;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/4/7 10:02
 */
public interface ApplyHelper {

    void sendApplyFeishu(Long nodeId, ApplyEntity applyEntity);

}
