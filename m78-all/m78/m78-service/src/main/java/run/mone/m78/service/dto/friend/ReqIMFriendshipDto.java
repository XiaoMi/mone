package run.mone.m78.service.dto.friend;


import lombok.Data;
import lombok.experimental.SuperBuilder;
import run.mone.m78.service.bo.BaseMessage;
import run.mone.m78.service.dao.entity.IMFriendshipStatusEnum;

import java.io.Serializable;

/**
 * @author wmin
 * @date 2024/5/9
 */
@Data
@SuperBuilder
public class ReqIMFriendshipDto extends BaseMessage implements Serializable {
    private Integer appId;

    private String currentUserName;

    private String friendUserName;

    /**
     * @see IMFriendshipStatusEnum
     */
    private int action;

}
