package run.mone.m78.service.dto.friend;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author wmin
 * @date 2024/5/20
 */
@Data
@Builder
public class MessageUnreadDto implements Serializable {

    private String friendUsername;

    private int unreadCount;
}
