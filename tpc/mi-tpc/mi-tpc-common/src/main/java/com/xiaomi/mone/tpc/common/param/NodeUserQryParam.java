package com.xiaomi.mone.tpc.common.param;

import com.xiaomi.mone.tpc.common.enums.NodeUserRelTypeEnum;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/3 19:52
 */
@Data
@ToString(callSuper = true)
public class NodeUserQryParam extends BaseParam implements Serializable {

    private Long nodeId;
    private Long outId;
    private Integer outIdType;
    private Long id;
    private Integer type;
    private Long memberId;
    private Integer tester;
    private Integer extType;
    @Override
    public boolean argCheck() {
        if (type != null && NodeUserRelTypeEnum.getEnum(type) == null) {
            return false;
        }
        return true;
    }
}
