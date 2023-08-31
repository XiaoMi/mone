/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.xiaomi.mone.monitor.service.model;

import lombok.Data;

/**
 *
 * @author zhanggaofeng1
 */
@Data
public class UserInfo {

    private String user;//用户账号
    private String name;//名称
    private String displayName;//展示名称
    private String departmentName;//部门
    private String firstDepartment;//一级部门（用于区分用户空间）
    private String secondDepartment;//二级部门（用于区分用户空间）
    private String email;//邮箱
    private String miID;//米id
    private String avatar;//头像图片
    private Boolean isAdmin;
    
}
