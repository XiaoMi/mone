package run.mone.m78.service.dto.friend;


import lombok.Data;
import lombok.experimental.SuperBuilder;
import run.mone.m78.service.bo.BaseMessage;

import java.io.Serializable;
import java.util.List;

/**
 * @author wmin
 * @date 2024/5/9
 */
@Data
@SuperBuilder
public class MessageReadDto extends BaseMessage implements Serializable {
    private Integer appId;

    private String currentUserName;

    private String friendUserName;

    private List<Integer> msgIds;

}
