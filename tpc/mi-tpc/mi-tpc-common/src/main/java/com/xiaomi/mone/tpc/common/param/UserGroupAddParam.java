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
public class UserGroupAddParam extends BaseParam {

    private String groupName;
    private String desc;

    @Override
    public boolean argCheck() {
        if (StringUtils.isBlank(groupName)) {
            return false;
        }
        return true;
    }
}
