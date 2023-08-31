package com.xiaomi.mone.tpc.common.param;

import com.xiaomi.mone.tpc.common.enums.ResourceStatusEnum;
import com.xiaomi.mone.tpc.common.enums.ResourceTypeEnum;
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
public class ResourceEditParam extends BaseParam {

    private Long id;
    private Integer type;
    private String resourceName;
    private String desc;
    private Integer status;
    Map<String, Object> param;

    private Integer isOpenKc;

    private String sid;

    private String kcUser;

    private String mfa;
    private ArgCheck arg;


    @Override
    public boolean argCheck() {
        if (id == null) {
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
        return true;
    }
}
