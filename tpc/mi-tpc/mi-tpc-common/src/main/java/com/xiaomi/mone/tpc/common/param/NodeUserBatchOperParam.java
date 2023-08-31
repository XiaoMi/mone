package com.xiaomi.mone.tpc.common.param;

import com.xiaomi.mone.tpc.common.enums.NodeTypeEnum;
import com.xiaomi.mone.tpc.common.enums.NodeUserRelTypeEnum;
import com.xiaomi.mone.tpc.common.enums.UserTypeEnum;
import lombok.Data;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.List;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/3 19:52
 */
@Data
@ToString(callSuper = true)
public class NodeUserBatchOperParam extends BaseParam implements Serializable {

    private Long memberId;
    private String memberAcc;
    private Integer memberAccType;
    private Integer type;
    private Integer tester;
    private List<Long> delNodeIds;
    private List<Long> addNodeIds;
    private Integer nodeType;

    @Override
    public boolean argCheck() {
        if (memberId == null && (StringUtils.isEmpty(memberAcc) || memberAccType == null)) {
            return false;
        }
        if (memberAccType != null && UserTypeEnum.getEnum(memberAccType) == null) {
            return false;
        }
        if (type == null || NodeUserRelTypeEnum.getEnum(type) == null) {
            return false;
        }
        if (nodeType == null || NodeTypeEnum.getEnum(nodeType) == null) {
            return false;
        }
        if (tester == null) {
            tester = 0;
        }
        return true;
    }
}
