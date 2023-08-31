package com.xiaomi.mone.tpc.common.param;

import com.xiaomi.mone.tpc.common.enums.OutIdTypeEnum;
import com.xiaomi.mone.tpc.common.enums.ResourceTypeEnum;
import lombok.Data;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

/**
 * @project: mi-tpc
 * @author: zhangxiaowei6
 * @date: 2022/3/23
 */
@Data
@ToString(callSuper = true)
public class ResourceGetTypeListParam extends BaseParam{

    private String type;  //资源类型,例如mysql
    private Long id;
    private Integer region;

    @Override
    public boolean argCheck(){
        if (StringUtils.isBlank(type)) {
            return false;
        }
        if (id == null || id <= 0) {
            return false;
        }
        ResourceTypeEnum resourceTypeEnum = ResourceTypeEnum.getEnumByString(type);
        if (resourceTypeEnum == null || resourceTypeEnum.getClazz() == null) {
            return false;
        }
        return true;
    }
}
