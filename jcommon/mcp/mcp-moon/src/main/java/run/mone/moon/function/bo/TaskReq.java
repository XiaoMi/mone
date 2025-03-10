package run.mone.moon.function.bo;

import lombok.Data;

import java.io.Serializable;

@Data
public class TaskReq implements Serializable {
    private String name;
    private String description;
    private long projectID;
    private String type;
    private String execMode;
    private String priority;
    private String execParam;
    private int retryWait;
    private int concurrency;
    private String machine;

    private String scheduleMode;
    private String scheduleParam;
    private Long startTime;
    private String concurrencyStrategy;

    // 报警信息
    private Boolean alertTimeout;
    private String alertTimeoutLevel;
    private Long timeout;
    private Boolean timeoutHalt;
    private Boolean alertSuccess;
    private String alertSuccessLevel;
    private Boolean alertFail;
    private String alertFailLevel;
    private Boolean alertStop;
    private String alertStopLevel;
    private Boolean alertSkip;
    private String alertSkipLevel;
    private int maxRetry;
    private Boolean alertNoMachine;
    private String alertNoMachineLevel;
    private String alertConfig;
    private Integer historyKeep;

    private FaasParam faasParam;
    private HttpParam httpParam;
    private DubboParam dubboParam;
    private Long id;
    private String userName;
    private Long mischeduleID;
}
