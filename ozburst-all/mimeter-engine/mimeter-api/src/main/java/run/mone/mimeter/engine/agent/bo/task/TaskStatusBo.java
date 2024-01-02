package run.mone.mimeter.engine.agent.bo.task;

import lombok.Data;

import java.io.Serializable;

@Data
public class TaskStatusBo implements Serializable {

    private int taskId;

    private int sceneId;

    private TaskStatus taskStatus;

}
