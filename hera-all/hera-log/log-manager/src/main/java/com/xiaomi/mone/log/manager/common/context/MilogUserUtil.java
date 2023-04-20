package com.xiaomi.mone.log.manager.common.context;

import com.xiaomi.mone.log.api.enums.ProjectSourceEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/9/14 10:58
 */
@Slf4j
public class MilogUserUtil {

    /**
     * @return
     */
    public static boolean isOne() {
        try {
            return StringUtils.equalsIgnoreCase(MoneUserContext.getCurrentUser().getZone(), ProjectSourceEnum.ONE_SOURCE.getSource());
        } catch (Exception e) {
            log.error("query user zone error,is one", e);
        }
        return false;
    }

    /**
     * @return
     */
    public static boolean isTwo() {
        try {
            return StringUtils.equalsIgnoreCase(MoneUserContext.getCurrentUser().getZone(), ProjectSourceEnum.TWO_SOURCE.getSource());
        } catch (Exception e) {
            log.error("query user zone error, is two", e);
        }
        return false;
    }
}
