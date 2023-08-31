package com.xiaomi.mone.tpc.common.param;

import com.xiaomi.mone.tpc.common.enums.OutIdTypeEnum;
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
public class ResourceGetResourceOrderByType extends BaseParam{
    private String resourceIds;
    private Long id;

    @Override
    public boolean argCheck(){
        if (StringUtils.isBlank(resourceIds)) {
            return false;
        }
        if (id == null || id <= 0) {
            return false;
        }
        return true;
    }
}
