package com.xiaomi.mone.log.manager.service.path;

import com.xiaomi.mone.app.enums.ProjectTypeEnum;
import com.xiaomi.youpin.docean.anno.Service;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2022/11/15 19:37
 */
@Service
public class LogPathMappingFactory {

    @Resource
    private MoneLogPathMapping moneLogPathMapping;
    @Resource
    private MilineK8sLogPathMapping milineK8sLogPathMapping;

    public LogPathMapping queryLogPathMappingByAppType(Integer code) {
        if (Objects.equals(ProjectTypeEnum.MIONE_TYPE.getCode(), code)) {
            return moneLogPathMapping;
        }
//        if (Objects.equals(ProjectTypeEnum.MILE_TYPE.getCode(), code)) {
//            return milineK8sLogPathMapping;
//        }
        return null;
    }
}
