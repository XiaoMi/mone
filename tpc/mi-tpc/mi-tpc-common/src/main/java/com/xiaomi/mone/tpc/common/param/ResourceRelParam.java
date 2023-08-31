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
public class ResourceRelParam extends BaseParam implements Serializable {

    private Long id;     //id
    private Long resourceId; //资源id

    @Override
    public boolean argCheck() {
        if (id == null || id <= 0) {
            return false;
        }
        if (resourceId == null || resourceId <= 0) {
            return false;
        }
        return true;
    }
}
