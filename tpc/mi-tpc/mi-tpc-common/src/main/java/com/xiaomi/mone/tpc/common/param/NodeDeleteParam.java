package com.xiaomi.mone.tpc.common.param;

import com.xiaomi.mone.tpc.common.enums.OutIdTypeEnum;
import lombok.Data;
import lombok.ToString;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/3 19:52
 */
@Data
@ToString(callSuper = true)
public class NodeDeleteParam extends BaseParam {

    private Long id;
    private Long outId;
    private Integer outIdType;

    @Override
    public boolean argCheck() {
        if (id == null && (outId == null || outId.equals(0L) || outIdType == null)) {
            return false;
        }
        if (outIdType != null && OutIdTypeEnum.getEnum(outIdType) == null) {
            return false;
        }
        return true;
    }
}
