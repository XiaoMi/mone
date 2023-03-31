package com.xiaomi.mone.tpc.common.vo;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/6/17 19:46
 */
@ToString
@Data
public class IamResDataVo implements Serializable {
    private Integer code;
    private String message;
    private String userMessage;
    private String level;
    private IamResInfoVo data;
}
