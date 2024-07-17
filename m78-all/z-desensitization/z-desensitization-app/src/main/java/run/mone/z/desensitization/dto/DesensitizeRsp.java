package run.mone.z.desensitization.dto;

import lombok.Builder;
import lombok.Data;

/**
 * @author wmin
 * @date 2024/1/31
 */
@Data
@Builder
public class DesensitizeRsp {
    private String textBefore;

    private String textAfter;

    private String username;

    private int status;

    private long durationTime;
}
