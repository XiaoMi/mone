package run.mone.mimeter.dashboard.bo;

import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
public class TaskResult implements Serializable {
    /**
     * 任务id
     */
    private int id;

    private int code;

    private String result;

    private boolean ok;

    /**
     * 返回的header
     */
    private Map<String,String> respHeaders;

    private String triggerCpInfo;

    private long rt;

    //bytes
    private long size;

}
