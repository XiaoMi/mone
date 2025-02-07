package run.mone.m78.service.bo.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @description
 * @version 1.0
 * @author wtt
 * @date 2024/4/29 15:54
 *
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserLoginRes {
    private Integer appId;
    private String userName;
    private String authKeyHash;
    private String token;
}
