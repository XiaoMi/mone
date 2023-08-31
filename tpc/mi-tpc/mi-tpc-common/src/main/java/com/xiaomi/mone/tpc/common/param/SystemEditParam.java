package com.xiaomi.mone.tpc.common.param;

import com.xiaomi.mone.tpc.common.enums.SystemStatusEnum;
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
public class SystemEditParam extends BaseParam {

    private Long id;
    private String systemName;
    private Integer status;
    private String desc;

    @Override
    public boolean argCheck() {
        if (id == null) {
            return false;
        }
        if (StringUtils.isBlank(systemName)) {
            return false;
        }
        if (status != null && SystemStatusEnum.getEnum(status) == null) {
            return false;
        }
        return true;
    }
}
