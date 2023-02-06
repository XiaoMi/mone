package com.xiaomi.mone.tpc.common.vo;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

@ToString
@Data
public class LoginInfoVo implements Serializable{

    private List<AuthAccountVo> authAccountVos;

}
