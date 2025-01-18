package run.mone.m78.service.dto.friend;


import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author wmin
 * @date 2024/5/9
 */
@Data
@Builder
public class ReqIMFriendDto implements Serializable {
    private Integer appId;

    private String userName;

    private Boolean isOnline;

}
