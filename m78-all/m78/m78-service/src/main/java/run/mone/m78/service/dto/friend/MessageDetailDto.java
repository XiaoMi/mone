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
public class MessageDetailDto implements Serializable {

    private Integer id;

    private Integer status;

    private String message;

    private String sender;

    private Timestamp sendTime;

}
