package com.xiaomi.mone.tpc.login.vo;

import com.xiaomi.mone.tpc.login.util.UserUtil;

public class AuthUserVo {
    private String account;
    private Integer userType;
    private Integer exprTime;//秒
    private String token;
    private String avatarUrl;
    private String name;
    private String setCookUrl;
    private String email;
    private String casUid;
    private String departmentName;
    private String loginUrl;
    private String logoutUrl;
    private String code;
    private String state;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public Integer getUserType() {
        return userType;
    }

    public void setUserType(Integer userType) {
        this.userType = userType;
    }

    public Integer getExprTime() {
        return exprTime;
    }

    public void setExprTime(Integer exprTime) {
        this.exprTime = exprTime;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSetCookUrl() {
        return setCookUrl;
    }

    public void setSetCookUrl(String setCookUrl) {
        this.setCookUrl = setCookUrl;
    }

    public String getCasUid() {
        return casUid;
    }

    public void setCasUid(String casUid) {
        this.casUid = casUid;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getLoginUrl() {
        return loginUrl;
    }

    public void setLoginUrl(String loginUrl) {
        this.loginUrl = loginUrl;
    }

    public String getLogoutUrl() {
        return logoutUrl;
    }

    public void setLogoutUrl(String logoutUrl) {
        this.logoutUrl = logoutUrl;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String genFullAccount() {
        return UserUtil.getFullAccount(account, userType);
    }

    @Override
    public String toString() {
        return "AuthUserVo{" +
                "account='" + account + '\'' +
                ", userType=" + userType +
                ", exprTime=" + exprTime +
                ", token='" + token + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                ", name='" + name + '\'' +
                ", casUid='" + casUid + '\'' +
                ", departmentName='" + departmentName + '\'' +
                ", setCookUrl='" + setCookUrl + '\'' +
                ", loginUrl='" + loginUrl + '\'' +
                ", code='" + code + '\'' +
                ", state='" + state + '\'' +
                ", logoutUrl='" + logoutUrl + '\'' +
                '}';
    }
}
