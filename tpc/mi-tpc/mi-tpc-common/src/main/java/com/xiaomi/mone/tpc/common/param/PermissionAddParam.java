package com.xiaomi.mone.tpc.common.param;

import lombok.Data;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/3 19:52
 */
@Data
@ToString(callSuper = true)
public class PermissionAddParam extends BaseParam {

    private Long systemId;
    private String permissionName;
    private String path;
    private String desc;

    @Override
    public boolean argCheck() {
        if (systemId == null) {
            return false;
        }
        if (StringUtils.isBlank(permissionName)){
            return false;
        }
        if (StringUtils.isBlank(path)){
            return false;
        }
        return true;
    }
}
