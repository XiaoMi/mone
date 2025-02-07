package run.mone.m78.service.bo.user;

import lombok.Data;

@Data
public class UserInfoVo {

    private String fullAccount;
    private int userType;
    private String username;//用户账号
    private String name;//名称
    private String displayName;//展示名称
    private String email;//邮箱
    private String id;//id
    private String avatar;//头像图片
    private boolean admin;
    private String tenant;
    private String zToken;
    private String logoutUrl;

}
