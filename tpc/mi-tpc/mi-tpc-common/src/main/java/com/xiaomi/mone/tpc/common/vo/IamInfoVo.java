package com.xiaomi.mone.tpc.common.vo;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/6/28 16:12
 */
@ToString
@Data
public class IamInfoVo implements Serializable {
    private Long id;
    private String name;
    private List<IamInfoVo> children;

    public IamInfoVo() {}

    public IamInfoVo(Long id, String name) {
        this.id = id;
        this.name = name;
    };
}
