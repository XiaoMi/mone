package com.xiaomi.mone.monitor.service.model.prometheus;

import com.xiaomi.mone.monitor.bo.AppLanguage;
import com.xiaomi.mone.monitor.bo.AppType;
import lombok.Data;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * @author zhangxiaowei6
 * @date 2022/3/30
 * */
@Data
@ToString(callSuper = true)
public class CreateTemplateParam  implements Serializable {
    private String name;
    private String template;
    private Integer platform;
    private Integer language;
    private Integer appType;
    private String urlParam;
    private Long id;
    private String panelIdList;

    public boolean check() {
        if (AppLanguage.getEnum(language) == null) {
            return false;
        }
        if (AppType.getEnum(appType) == null) {
            return false;
        }
        if (StringUtils.isBlank(name)) {
            return false;
        }
        if (StringUtils.isBlank(panelIdList)) {
            return false;
        }
        if (StringUtils.isBlank(template)) {
            return false;
        }
        return true;
    }

}
