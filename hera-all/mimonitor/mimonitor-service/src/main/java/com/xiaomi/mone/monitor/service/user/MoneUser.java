package com.xiaomi.mone.monitor.service.user;

import com.google.gson.annotations.SerializedName;
import lombok.Data;
import lombok.ToString;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2021/9/2 15:59
 */
@Data
@ToString
public class MoneUser {

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

    /**
     * miID
     */
    @SerializedName("cas:uid")
    private String uID;

    /**
     * 头像
     */
    @SerializedName("cas:avatar")
    private String avatar;

    private Boolean isAdmin;

    /**
     * 来源 二级部门
     */
    private String zone;
    /**
     * 最后一级部门Id
     */
    private String deptId;

    private String company;


//    public String getSource(){
//        if(this.getDepartmentName().equals("研发效能不")
//    }
}
