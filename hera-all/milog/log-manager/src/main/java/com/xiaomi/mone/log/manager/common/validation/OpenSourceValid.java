package com.xiaomi.mone.log.manager.common.validation;

import com.google.common.collect.Lists;
import com.xiaomi.mone.log.api.model.vo.MiLogMoneEnv;
import com.xiaomi.youpin.docean.anno.Component;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

import static com.xiaomi.mone.log.common.Constant.SYMBOL_COMMA;

/**
 * @author: wtt
 * @date: 2022/5/24 18:32
 * @description: 第三方应用接入参数校验
 */
@Slf4j
@Component
public class OpenSourceValid {


    public String validMiLogMoneEnv(MiLogMoneEnv logMoneEnv) {
        List<String> errorInfos = Lists.newArrayList();
        if (null == logMoneEnv.getNewAppId() || null == logMoneEnv.getOldAppId()) {
            errorInfos.add("appId 不能为空");
        }
        if (null == logMoneEnv.getNewEnvId() || null == logMoneEnv.getOldEnvId()) {
            errorInfos.add("envId 不能为空");
        }
        if (StringUtils.isBlank(logMoneEnv.getNewAppName()) ||
                StringUtils.isBlank(logMoneEnv.getOldAppName())) {
            errorInfos.add("appName 不能为空");
        }
        if (StringUtils.isBlank(logMoneEnv.getNewEnvName()) ||
                StringUtils.isBlank(logMoneEnv.getOldEnvName())) {
            errorInfos.add("envName 不能为空");
        }
        return errorInfos.stream().collect(Collectors.joining(SYMBOL_COMMA));
    }
}
