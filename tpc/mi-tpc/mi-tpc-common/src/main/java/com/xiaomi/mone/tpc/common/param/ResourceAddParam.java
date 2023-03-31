package com.xiaomi.mone.tpc.common.param;

import com.xiaomi.mone.tpc.common.enums.*;
import com.xiaomi.mone.tpc.common.util.GsonUtil;
import lombok.Data;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/3 19:52
 */
@Data
@ToString(callSuper = true)
public class ResourceAddParam extends BaseParam {

    private Long nodeId;
    private String resourceName;
    private String desc;
    private Integer type;
    private Integer status;
    Map<String, Object> param;
    private ArgCheck arg;
    private Integer envFlag;

    private Integer isOpenKc;

    private String sid;

    private String kcUser;

    private String mfa;
    private Integer region;

    @Override
    public boolean argCheck() {
        if (nodeId == null || nodeId <=0 ) {
            return false;
        }
        if (envFlag == null || NodeEnvFlagEnum.getEnum(envFlag) == null) {
            return false;
        }
        if (StringUtils.isBlank(resourceName)) {
            return false;
        }
        if (status != null && ResourceStatusEnum.getEnum(status) == null) {
            return false;
        }
        ResourceTypeEnum resourceTypeEnum = ResourceTypeEnum.getEnum(type);
        if (resourceTypeEnum == null || resourceTypeEnum.getClazz() == null) {
            return false;
        }
        arg = GsonUtil.gsonToBean(GsonUtil.gsonString(param), resourceTypeEnum.getClazz());
        if (arg == null || !arg.argCheck()) {
            return false;
        }
        if (region != null && ResourceRegionEnum.getEnum(region) == null) {
            return false;
        }
        if (region == null) {
            region = ResourceRegionEnum.CZONE.getCode();
        }
        return true;
    }
}
