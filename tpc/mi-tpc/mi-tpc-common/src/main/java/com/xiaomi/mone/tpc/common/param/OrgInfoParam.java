package com.xiaomi.mone.tpc.common.param;

import lombok.Data;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/3 17:12
 */
@ToString
@Data
public class OrgInfoParam implements ArgCheck, Serializable {
    private String idPath;
    private String namePath;
    private String[] ids;
    private String[] names;

    @Override
    public boolean argCheck() {
        if (StringUtils.isBlank(idPath) || StringUtils.isBlank(namePath)) {
            return false;
        }
        this.ids = idPath.split("\\/");
        this.names = namePath.split("\\/");
        if (ids.length != names.length) {
            return false;
        }
        return true;
    }
}
