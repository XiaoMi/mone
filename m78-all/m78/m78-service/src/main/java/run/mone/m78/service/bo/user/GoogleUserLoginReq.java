package run.mone.m78.service.bo.user;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 *
 * @description
 * @version 1.0
 * @author wtt
 * @date 2024/4/29 10:32
 *
 */
@Data
public class GoogleUserLoginReq {

    //todo 先兼容@NotNull
    private Integer appId;

    @NotBlank
    private String jwtToken;

}
