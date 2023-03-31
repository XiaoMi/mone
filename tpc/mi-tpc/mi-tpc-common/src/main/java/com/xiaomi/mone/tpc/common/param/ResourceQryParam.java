package com.xiaomi.mone.tpc.common.param;

import com.xiaomi.mone.tpc.common.enums.*;
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
public class ResourceQryParam extends BaseParam implements Serializable {

    private Long id;
    private Long poolNodeId;
    private Long relNodeId;
    private Long relOutId;
    private Integer relOutIdType;
    private Long applyId;
    private Integer type;
    private Integer status;
    private String resourceName;
    private String key1;
    private Integer envFlag;
    private Integer region;

    @Override
    public boolean argCheck() {
        if (type != null && ResourceTypeEnum.getEnum(type) == null) {
            return false;
        }
        if (status != null && ResourceStatusEnum.getEnum(status) == null) {
            return false;
        }
        if (envFlag != null && NodeEnvFlagEnum.getEnum(envFlag) == null) {
            return false;
        }
        if (relOutIdType != null && OutIdTypeEnum.getEnum(relOutIdType) == null) {
            return false;
        }
        return true;
    }
}
