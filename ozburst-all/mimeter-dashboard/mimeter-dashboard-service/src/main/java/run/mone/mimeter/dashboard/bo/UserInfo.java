package run.mone.mimeter.dashboard.bo;

import lombok.Data;

@Data
public class UserInfo {
    private String fullAccount;//
    private String username;//用户账号
    private String name;//名称
    private String displayName;//展示名称
    private String departmentName;//部门
    private String firstDepartment;//一级部门（用于区分用户空间）
    private String secondDepartment;//二级部门（用于区分用户空间）
    private String email;//邮箱
    private String miID;//米id
    private String avatar;//头像图片
    private boolean admin;
    
}
