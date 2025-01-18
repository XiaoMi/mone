package run.mone.m78.api.bo.multiModal;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.List;

/**
 * @author wmin
 * @date 2024/7/25
 */
@Data
public class WanxTaskNotifyReq implements Serializable {

    private String taskId;

    /**
     * 1 成功 2 失败
     */
    private int taskStatus;

    private String message;

    private List<String> resultUrl;

    public Pair<Boolean, String> validateReq() {
        if (StringUtils.isBlank(this.taskId)) {
            return Pair.of(false, "taskId cannot be null");
        }
        if (taskStatus != 1 && taskStatus != 2) {
            return Pair.of(false, "taskStatus is invalid");
        }
        if (taskStatus == 1 && CollectionUtils.isEmpty(resultUrl)){
            return Pair.of(false, "resultUrl is empty");
        }
        return Pair.of(true, "Validation passed");
    }
}
