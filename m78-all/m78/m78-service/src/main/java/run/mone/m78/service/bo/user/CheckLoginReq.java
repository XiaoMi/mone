package run.mone.m78.service.bo.user;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author wmin
 * @date 2024/5/13
 */
@Data
@Builder
public class CheckLoginReq implements Serializable {

    private Integer appId;

    private String token;

}
