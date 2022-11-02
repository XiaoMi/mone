package com.xiaomi.mone.tpc.login.vo;

import com.google.gson.annotations.SerializedName;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/10/8 10:34
 */
public class UserInfoVO {

    /**
     * 用户名，如：zhangsan
     */
    @SerializedName("cas:user")
    private String user;

    /**
     * 用户名，如：张三
     */
    @SerializedName("cas:name")
    private String name;

    /**
     * 用户展示名，如：sa zhang 张三
     */
    @SerializedName("cas:displayName")
    private String displayName;

    /**
     * 部门名称
     */
    @SerializedName("cas:departmentName")
    private String departmentName;

    /**
     * 邮箱
     */
    @SerializedName("cas:email")
    private String email;

    /**
     * miID
     */
    @SerializedName("cas:miID")
    private String miID;

    @SerializedName("cas:uid")
    private String uID;

    /**
     * 头像
     */
    @SerializedName("cas:avatar")
    private String avatar;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDepartmentName() {
        return departmentName;
    }

    public void setDepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMiID() {
        return miID;
    }

    public void setMiID(String miID) {
        this.miID = miID;
    }

    public String getuID() {
        return uID;
    }

    public void setuID(String uID) {
        this.uID = uID;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
