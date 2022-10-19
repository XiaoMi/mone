package com.xiaomi.mone.tpc.login.vo;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.ToString;

/**
 * @project: mi-tpc
 * @author: zgf1
 * @date: 2022/10/8 10:34
 */
@Data
@ToString
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

}
