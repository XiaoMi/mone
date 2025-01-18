package run.mone.m78.service.bo.user;

import lombok.Data;

/**
 * @author zhangxiaowei6
 * @Date 2024/3/15 16:13
 */

@Data
public class UserCollectReq {

    private int type;

    private long collectId;

    private String userName;

    private Integer appId;

    private String token;

}
