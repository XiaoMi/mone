package run.mone.mimeter.dashboard.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Xirui Yang (yangxirui@xiaomi.com)
 * @version 1.0
 * @since 2022/6/30
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReportEmitterData {

    /**
     * EmitterTypeEnum
     */
    private String type;

    /**
     * create time
     */
    private Long ts;

    /**
     * json data point
     */
    private String data;

    /**
     * used for machine node or api
     */
    private String node;

    private String extra;
}
