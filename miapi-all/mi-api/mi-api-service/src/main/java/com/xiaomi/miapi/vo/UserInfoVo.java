package com.xiaomi.miapi.vo;

import com.xiaomi.youpin.hermes.bo.RoleBo;
import com.xiaomi.youpin.hermes.bo.response.Account;

import java.util.List;

public class UserInfoVo extends Account {
    private  List<RoleBo> roleBos;

    public List<RoleBo> getRoleBos() {
        return roleBos;
    }

    public void setRoleBos(List<RoleBo> roleBos) {
        this.roleBos = roleBos;
    }
}
