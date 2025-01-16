package run.mone.m78.service.bo.user;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 *
 * @description
 * @version 1.0
 * @author wtt
 * @date 2024/4/29 10:32
 *
 */
@Data
public class UserLoginReq {

    //todo 先兼容@NotNull
    private Integer appId;

    @NotBlank
    private String userName;

    private String password;

    private String token;
}
