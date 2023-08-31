package com.xiaomi.mone.tpc.common.vo;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/3/3 17:12
 */
@ToString
@Data
public class OrgInfoVo implements Serializable {
    private String namePath;
    private String idPath;

    public OrgInfoVo() {}

    public OrgInfoVo(String namePath, String idPath) {
        this.namePath = namePath;
        this.idPath = idPath;
    };
}
