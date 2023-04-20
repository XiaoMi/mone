package com.xiaomi.mone.log.manager.service.impl;

import com.google.gson.Gson;
import com.xiaomi.mone.log.api.enums.DeployWayEnum;
import com.xiaomi.mone.log.api.enums.ProjectSourceEnum;
import com.xiaomi.mone.log.api.enums.ProjectTypeEnum;
import com.xiaomi.mone.log.api.model.bo.MiLogMoneTransfer;
import com.xiaomi.mone.log.api.model.dto.MontorAppDTO;
import com.xiaomi.mone.log.api.model.vo.MiLogMoneEnv;
import com.xiaomi.mone.log.api.service.MilogOpenService;
import com.xiaomi.mone.log.manager.common.exception.MilogManageException;
import com.xiaomi.mone.log.manager.common.validation.OpenSourceValid;
import com.xiaomi.mone.log.manager.dao.MilogAppTopicRelDao;
import com.xiaomi.mone.log.manager.dao.MilogLogTailDao;
import com.xiaomi.mone.log.manager.model.pojo.MilogAppTopicRelDO;
import com.xiaomi.mone.log.manager.model.pojo.MilogLogTailDo;
import com.xiaomi.youpin.docean.plugin.dubbo.anno.Service;
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
    private MilogAppTopicRelDao milogAppTopicRelDao;

    @Resource
    private MilogLogTailDao milogLogtailDao;

    @Resource
    private OpenSourceValid openSourceValid;

    @Resource
    private Gson gson;


    @Override
    public MontorAppDTO queryHaveAccessMilog(Long iamTreeId) {
        MontorAppDTO montorAppDTO = new MontorAppDTO();
        if (null == iamTreeId) {
            return montorAppDTO;
        }
        MilogAppTopicRelDO appTopicRel = milogAppTopicRelDao.queryByIamTreeId(iamTreeId);
        if (null == appTopicRel) {
            return montorAppDTO;
        }
        List<MilogLogTailDo> milogLogtailDos = milogLogtailDao.queryByAppId(appTopicRel.getId());
        if (CollectionUtils.isNotEmpty(milogLogtailDos)) {
            montorAppDTO.setAppId(appTopicRel.getAppId());
            montorAppDTO.setAppName(appTopicRel.getAppName());
            montorAppDTO.setSource(appTopicRel.getSource());
            montorAppDTO.setIsAccess(true);
        }
        return montorAppDTO;
    }

    @Override
    public Long querySpaceIdByIamTreeId(Long iamTreeId) {
        MilogAppTopicRelDO appTopicRel = milogAppTopicRelDao.queryByIamTreeId(iamTreeId);
        if (null == appTopicRel) {
            return null;
        }
        List<MilogLogTailDo> milogLogtailDos = milogLogtailDao.queryByAppId(appTopicRel.getId());
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
        //3.修改应用的来源
        handleAppSource(logMoneEnv, miLogMoneTransfer);
        return miLogMoneTransfer;
    }

    private void handleAppSource(MiLogMoneEnv logMoneEnv, MiLogMoneTransfer miLogMoneTransfer) {
        MilogAppTopicRelDO appTopicRelDO = milogAppTopicRelDao.queryById(miLogMoneTransfer.getMilogAppId());
        if (Objects.equals(1, logMoneEnv.getRollback())) {
            appTopicRelDO.setSource(ProjectSourceEnum.TWO_SOURCE.getSource());
        } else {
            appTopicRelDO.setSource(ProjectSourceEnum.ONE_SOURCE.getSource());
        }
        milogAppTopicRelDao.update(appTopicRelDO);
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
        MilogAppTopicRelDO milogAppTopicRel = milogAppTopicRelDao.queryByAppId(logMoneEnv.getOldAppId(), ProjectTypeEnum.MIONE_TYPE.getCode());
        if (null == milogAppTopicRel) {
            milogAppTopicRel = milogAppTopicRelDao.queryByAppId(logMoneEnv.getNewAppId(), ProjectTypeEnum.MIONE_TYPE.getCode());
            if (null == milogAppTopicRel) {
                throw new MilogManageException("应用不存在");
            }
        }
        if (!Objects.equals(milogAppTopicRel.getAppId(), logMoneEnv.getNewAppId()) ||
                !Objects.equals(milogAppTopicRel.getAppName(), logMoneEnv.getNewAppName())) {
            milogAppTopicRel.setAppId(logMoneEnv.getNewAppId());
            milogAppTopicRel.setAppName(logMoneEnv.getNewAppName());
            milogAppTopicRelDao.update(milogAppTopicRel);
        }

        miLogMoneTransfer.setMilogAppId(milogAppTopicRel.getId());
        miLogMoneTransfer.setAppId(logMoneEnv.getNewAppId());
        miLogMoneTransfer.setAppName(logMoneEnv.getNewAppName());
    }
}
