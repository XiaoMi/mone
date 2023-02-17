package com.xiaomi.mone.log.api.service;

import com.xiaomi.mone.log.api.model.bo.MiLogMoneTransfer;
import com.xiaomi.mone.log.api.model.dto.MontorAppDTO;
import com.xiaomi.mone.log.api.model.vo.MiLogMoneEnv;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/12/8 14:53pom
 */
public interface MilogOpenService {
    /**
     * 查询是否接入了日志系统
     *
     * @param iamTreeId
     * @return
     */
    MontorAppDTO queryHaveAccessMilog(Long iamTreeId);

    /**
     * 查询接入的spaceId 最后一个
     *
     * @param iamTreeId
     * @return
     */
    Long querySpaceIdByIamTreeId(Long iamTreeId);

    /**
     * mione迁移 数据清洗
     */
    MiLogMoneTransfer ypMoneEnvTransfer(MiLogMoneEnv logMoneEnv);
}
