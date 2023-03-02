package com.xiaomi.mone.log.manager.service.env;

import com.xiaomi.mone.log.api.enums.ProjectTypeEnum;
import com.xiaomi.youpin.docean.anno.Service;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/11/15 19:28
 */
@Service
public class HeraEnvIpServiceFactory {

    @Resource
    private MoneHeraEnvIpService moneHeraEnvIpService;

    @Resource
    private MisHeraEnvIpService misHeraEnvIpService;

    @Resource
    private MilineHeraEnvIpService milineHeraEnvIpService;

    public HeraEnvIpService getHeraEnvIpServiceByAppType(Integer code) {

        if (Objects.equals(ProjectTypeEnum.MIONE_TYPE.getCode(), code)) {
            return moneHeraEnvIpService;
        }
//        if (Objects.equals(ProjectTypeEnum.MIS_TYPE.getCode(), code)) {
//            return misHeraEnvIpService;
//        }
//        if (Objects.equals(ProjectTypeEnum.MILINE_TYPE.getCode(), code)) {
//            return milineHeraEnvIpService;
//        }
        return moneHeraEnvIpService;
    }

}
