package com.xiaomi.mone.tpc.common.param;

import com.xiaomi.mone.tpc.common.enums.OutIdTypeEnum;
import lombok.Data;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/3 19:52
 */
@Data
@ToString(callSuper = true)
public class NodeOrgEditParam extends BaseParam implements Serializable {

    private Long id;
    private OrgInfoParam orgParam;

    @Override
    public boolean argCheck() {
        if (id == null) {
            return false;
        }
        if (orgParam == null || !orgParam.argCheck()) {
            return false;
        }
        return true;
    }
}
