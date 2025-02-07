package run.mone.m78.service.bo.user;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author wmin
 * @date 2024/5/11
 */
@Data
@Builder
public class BizUserInfo implements Serializable {

    private Integer appId;

    private String userName;

    private long lastLoginTime;

}
