package com.xiaomi.mone.log.manager.service.impl;

import com.xiaomi.mone.log.api.enums.OperateEnum;
import com.xiaomi.mone.log.common.Constant;
import com.xiaomi.mone.log.manager.common.exception.MilogManageException;
import com.xiaomi.mone.log.manager.common.validation.StoreSpaceAuthValid;
import com.xiaomi.mone.log.manager.dao.MilogStoreSpaceAuthDao;
import com.xiaomi.mone.log.manager.model.bo.StoreSpaceAuth;
import com.xiaomi.mone.log.manager.model.pojo.MilogStoreSpaceAuth;
import com.xiaomi.mone.log.manager.service.BaseService;
import com.xiaomi.mone.log.manager.service.MilogStoreSpaceAuthService;
import com.xiaomi.youpin.docean.anno.Service;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Resource;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/7/14 16:13
 */
@Slf4j
@Service
public class MilogStoreSpaceAuthServiceImpl extends BaseService implements MilogStoreSpaceAuthService {

    @Resource
    private MilogStoreSpaceAuthDao milogStoreSpaceAuthDao;

    @Resource
    private StoreSpaceAuthValid storeSpaceAuthValid;

    @Override
    public String storeSpaceAuth(StoreSpaceAuth storeSpaceAuth) {
        // 校验参数
        String paramsErrorInfos = storeSpaceAuthValid.validParam(storeSpaceAuth);
        if (StringUtils.isNotBlank(paramsErrorInfos)) {
            throw new MilogManageException(paramsErrorInfos);
        }
        // 校验数据是否真实存在
        String dataCollectInfos = storeSpaceAuthValid.validStoreAuthData(storeSpaceAuth);
        if (StringUtils.isNotBlank(dataCollectInfos)) {
            throw new MilogManageException(dataCollectInfos);
        }
        // 是否存在，存在修改，不存在新增
        MilogStoreSpaceAuth milogStoreSpaceAuth = milogStoreSpaceAuthDao.queryByStoreSpace(storeSpaceAuth.getStoreId(), storeSpaceAuth.getSpaceId());
        if (null == milogStoreSpaceAuth) {
            MilogStoreSpaceAuth auth = buildStoreSpaceAuth(storeSpaceAuth.getStoreId(), storeSpaceAuth.getSpaceId());
            milogStoreSpaceAuthDao.add(auth);
            return Constant.SUCCESS_MESSAGE;
        }
        wrapBaseCommon(milogStoreSpaceAuth, OperateEnum.UPDATE_OPERATE);
        milogStoreSpaceAuthDao.update(milogStoreSpaceAuth);
        return Constant.SUCCESS_MESSAGE;
    }

    private MilogStoreSpaceAuth buildStoreSpaceAuth(Long storeId, Long spaceId) {
        MilogStoreSpaceAuth storeSpaceAuth = new MilogStoreSpaceAuth();
        storeSpaceAuth.setStoreId(storeId);
        storeSpaceAuth.setSpaceId(spaceId);
        wrapBaseCommon(storeSpaceAuth, OperateEnum.ADD_OPERATE);
        return storeSpaceAuth;
    }
}
