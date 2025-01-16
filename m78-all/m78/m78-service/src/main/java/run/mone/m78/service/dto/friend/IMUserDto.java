package run.mone.m78.service.dto.friend;


import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @author wmin
 * @date 2024/5/9
 */
@Data
@Builder
public class IMUserDto implements Serializable {

    private Integer appId;

    private Integer id;

    private String userName;

    private boolean isOnline;

    private Timestamp lastSeen;

    private Timestamp createTime;
}
