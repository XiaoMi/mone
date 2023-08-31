package com.xiaomi.mone.tpc.common.param;

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
public class ApplyAddParamV2 extends BaseParam implements Serializable {


    private String type;
    private String applyName;
    private String name;
    private Long nodeId;
    private String desc;

    @Override
    public boolean argCheck() {
        return true;
    }
}
