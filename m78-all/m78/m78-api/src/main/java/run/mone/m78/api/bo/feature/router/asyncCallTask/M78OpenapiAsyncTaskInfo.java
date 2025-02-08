package run.mone.m78.api.bo.feature.router.asyncCallTask;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class M78OpenapiAsyncTaskInfo {

    private Long id;

    private Integer type;

    private Long relateId;

    private String inputs;

    private String outputs;

    private String taskId;

    private Integer taskStatus;

    private String callbackUrl;

    private Integer callbackStatus;

    private Long invokeStartTime;

    private Long invokeEndTime;

    private String invokeUserName;

}
