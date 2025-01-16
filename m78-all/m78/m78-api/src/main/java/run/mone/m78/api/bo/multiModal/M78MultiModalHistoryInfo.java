package run.mone.m78.api.bo.multiModal;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author wmin
 * @date 2024/7/25
 */
@Data
@Builder
public class M78MultiModalHistoryInfo {
    private Long id;

    private Long workSpaceId;

    private String taskId;

    private Integer type;

    private String aiModel;

    private Integer deleted;

    private Integer runStatus;

    private String userName;

    private Long ctime;

    private Long utime;

    private List<String> multiModalResourceOutput;

    private String rstMessage;

    private Object setting;
}
