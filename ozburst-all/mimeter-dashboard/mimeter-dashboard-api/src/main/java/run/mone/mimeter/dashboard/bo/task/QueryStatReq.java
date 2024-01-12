package run.mone.mimeter.dashboard.bo.task;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Xirui Yang (yangxirui@xiaomi.com)
 * @version 1.0
 * @since 2022/7/21
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class QueryStatReq {

    private Long taskId;

    private Long startTs;

    private Long endTs;

    private String query;
}
