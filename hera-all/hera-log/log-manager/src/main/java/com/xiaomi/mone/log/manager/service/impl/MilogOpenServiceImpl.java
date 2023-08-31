/*
 * Copyright 2020 Xiaomi
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.xiaomi.mone.log.manager.service.impl;

import com.google.gson.Gson;
import com.xiaomi.mone.app.api.response.AppBaseInfo;
import com.xiaomi.mone.log.api.enums.DeployWayEnum;
import com.xiaomi.mone.log.api.enums.ProjectTypeEnum;
import com.xiaomi.mone.log.api.model.bo.MiLogMoneTransfer;
import com.xiaomi.mone.log.api.model.dto.MontorAppDTO;
import com.xiaomi.mone.log.api.model.vo.MiLogMoneEnv;
import com.xiaomi.mone.log.api.service.MilogOpenService;
import com.xiaomi.mone.log.manager.dao.MilogLogTailDao;
import com.xiaomi.mone.log.manager.common.exception.MilogManageException;
import com.xiaomi.mone.log.manager.model.pojo.MilogLogTailDo;
import com.xiaomi.mone.log.manager.common.validation.OpenSourceValid;
import com.xiaomi.youpin.docean.plugin.dubbo.anno.Service;
import joptsimple.internal.Strings;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/12/8 14:54
 */
@Slf4j
@Service(interfaceClass = MilogOpenService.class, group = "$dubbo.group")
public class MilogOpenServiceImpl implements MilogOpenService {

    @Resource
    private MilogLogTailDao milogLogtailDao;

    @Resource
    private OpenSourceValid openSourceValid;

    @Resource
    private HeraAppServiceImpl heraAppService;

    @Resource
    private Gson gson;


    @Override
    public MontorAppDTO queryHaveAccessMilog(Long iamTreeId, String bingId, Integer platformType) {
        MontorAppDTO montorAppDTO = new MontorAppDTO();
        if (null == iamTreeId) {
            return montorAppDTO;
        }
        AppBaseInfo appBaseInfo = heraAppService.queryByIamTreeId(iamTreeId, bingId, platformType);
        if (null == appBaseInfo) {
            return montorAppDTO;
        }
        List<MilogLogTailDo> logTailDos = milogLogtailDao.getLogTailByMilogAppId(appBaseInfo.getId().longValue());
        if (CollectionUtils.isNotEmpty(logTailDos)) {
            montorAppDTO.setAppId(Long.valueOf(appBaseInfo.getBindId()));
            montorAppDTO.setAppName(appBaseInfo.getAppName());
            montorAppDTO.setSource(appBaseInfo.getPlatformName());
            montorAppDTO.setIsAccess(true);
        }
        return montorAppDTO;
    }

    @Override
    public Long querySpaceIdByIamTreeId(Long iamTreeId) {
        AppBaseInfo appBaseInfo = heraAppService.queryByIamTreeId(iamTreeId, Strings.EMPTY, null);
        if (null == appBaseInfo) {
            return null;
        }
        List<MilogLogTailDo> milogLogtailDos = milogLogtailDao.getLogTailByMilogAppId(appBaseInfo.getId().longValue());
        if (CollectionUtils.isEmpty(milogLogtailDos)) {
            return null;
        }
        List<Long> spaceIds = milogLogtailDos.stream().map(MilogLogTailDo::getSpaceId).distinct().collect(Collectors.toList());
        return spaceIds.get(spaceIds.size() - 1);
    }

    @Override
    public MiLogMoneTransfer ypMoneEnvTransfer(MiLogMoneEnv logMoneEnv) {
        String errors = openSourceValid.validMiLogMoneEnv(logMoneEnv);
        if (StringUtils.isNotBlank(errors)) {
            throw new MilogManageException(errors);
        }
        log.info("youpin mione transfer milie,data:{}", gson.toJson(logMoneEnv));
        MiLogMoneTransfer miLogMoneTransfer = new MiLogMoneTransfer();
        //1.查找应用
        handleMilogAppInfo(logMoneEnv, miLogMoneTransfer);
        //2.修改tail
        handleMilogAppTail(logMoneEnv, miLogMoneTransfer);
        //3.修改应用的来源-迁移后不需要
//        handleAppSource(logMoneEnv, miLogMoneTransfer);
        return miLogMoneTransfer;
    }

    private void handleMilogAppTail(MiLogMoneEnv logMoneEnv, MiLogMoneTransfer miLogMoneTransfer) {
        // 查询旧的tail
        List<MilogLogTailDo> milogLogtailDos = milogLogtailDao.queryByMilogAppAndEnv(miLogMoneTransfer.getMilogAppId(), logMoneEnv.getOldEnvId());
        if (CollectionUtils.isEmpty(milogLogtailDos)) {
            return;
        }
        for (MilogLogTailDo milogLogtailDo : milogLogtailDos) {
            milogLogtailDo.setAppName(logMoneEnv.getNewAppName());
            milogLogtailDo.setEnvId(logMoneEnv.getNewEnvId());
            milogLogtailDo.setEnvName(logMoneEnv.getNewEnvName());
            if (Objects.equals(1, logMoneEnv.getRollback())) {
                milogLogtailDo.setDeployWay(DeployWayEnum.MIONE.getCode());
            }
            milogLogtailDao.update(milogLogtailDo);
        }
        miLogMoneTransfer.setEnvId(logMoneEnv.getNewEnvId());
        miLogMoneTransfer.setEnvName(logMoneEnv.getNewEnvName());
        miLogMoneTransfer.setTailNames(milogLogtailDos
                .stream()
                .map(MilogLogTailDo::getTail)
                .collect(Collectors.toList()));
    }

    private void handleMilogAppInfo(MiLogMoneEnv logMoneEnv, MiLogMoneTransfer miLogMoneTransfer) {
        //1.根据旧ID查找应用
        AppBaseInfo appBaseInfo = heraAppService.queryByAppId(logMoneEnv.getNewAppId(), ProjectTypeEnum.MIONE_TYPE.getCode());
        if (null == appBaseInfo) {
            // 兼容
            appBaseInfo = heraAppService.queryByAppId(logMoneEnv.getNewAppId(), 20);
            if (null == appBaseInfo) {
                throw new MilogManageException("应用不存在");
            }
        }
        miLogMoneTransfer.setMilogAppId(appBaseInfo.getId().longValue());
        miLogMoneTransfer.setAppId(logMoneEnv.getNewAppId());
        miLogMoneTransfer.setAppName(logMoneEnv.getNewAppName());
    }
}
