package com.xiaomi.mone.tpc.login.vo;

import com.xiaomi.mone.tpc.login.util.SignUtil;
import com.xiaomi.mone.tpc.login.util.UserUtil;
import lombok.Data;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/7/29 15:54
 */
@Data
@ToString
public class MoneTpcContext implements Serializable {

    private String sysName;
    private String sysSign;
    private String userToken;
    private long reqTime;
    private String account;
    private Integer userType;
    private Map<String, String> extMap;

    public static MoneTpcContext create(String sysName, String token, String userToken, Object param) throws Throwable {
        long now = new Date().getTime();
        MoneTpcContext context = new MoneTpcContext();
        context.sysName = sysName;
        context.reqTime = now;
        context.userToken = userToken;
        StringBuilder sysSignBuilder = new StringBuilder();
        sysSignBuilder.append(sysName).append(token).append(now);
        if (StringUtils.isNotBlank(userToken)) {
            sysSignBuilder.append(userToken);
        }
        String dataSign = SignUtil.getDataSign(param);
        if (StringUtils.isNotBlank(dataSign)) {
            sysSignBuilder.append(dataSign);
        }
        context.sysSign = sysSignBuilder.toString();
        return context;
    }


    /**
     * 生成全局唯一账号
     * @return
     */
    public String getFullAccount() {
        return UserUtil.getFullAccount(account, userType);
    }

    /**
     * 解析全局唯一账号
     * @param fullAccount
     * @return
     */
    public boolean parseFullAccount(String fullAccount) {
        AuthUserVo userVo = UserUtil.parseFullAccount(fullAccount);
        if (userVo == null) {
            return false;
        }
        this.account = userVo.getAccount();
        this.userType = userVo.getUserType();
        return true;
    }

    public void addExt(String key, String value) {
        if (extMap == null) {
            extMap = new HashMap<>();
        }
        extMap.put(key, value);
    }

    public String getExt(String key) {
        if (extMap == null) {
            return null;
        }
        return extMap.get(key);
    }

    public AuthUserVo buildAuthUserVo() {
        AuthUserVo userVo = new AuthUserVo();
        userVo.setAccount(this.account);
        userVo.setUserType(this.userType);
        return userVo;
    }

}
