package run.mone.moon.function.bo;

import lombok.Data;

import java.io.Serializable;

@Data
public class ReadTaskReq implements Serializable {
    private String taskName;

    private String taskType;

    private Long status;

    private String creator;

    private String servicePath;

    private int page;

    private int pageSize;

    private Long mischeduleID;

    private Long projectID;
}
