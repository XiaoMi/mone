package run.mone.moon.function.bo;

import lombok.Data;

import java.io.Serializable;

@Data
public class TaskListItem implements Serializable {
    private Long id;
    private Long projectId;
    private String name;
    private String projectName;
    private String status;
    private String type;
    private String execMode;
    private String priority;
    private String scheduleMode;
    private String scheduleParam;
    private String creator;
    private String updater;
    private String tenant;
    private Long createTimestamp;
    private Long updateTimestamp;
    private boolean enabled;
}
