package com.xiaomi.mone.tpc.login.common.vo;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@ToString
@Data
public class LoginInfoVo implements Serializable{

    private List<AuthAccountVo> authAccountVos;
    private String vcode;
    private String state;
    private String pageUrl;

}
