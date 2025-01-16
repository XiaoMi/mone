package run.mone.m78.service.dto.friend;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @author wmin
 * @date 2024/5/20
 */
@Data
@Builder
public class FriendReqDto implements Serializable {

    private Integer appId;

    private Integer userId;

    private String userName;

    private Timestamp reqTime;

}
