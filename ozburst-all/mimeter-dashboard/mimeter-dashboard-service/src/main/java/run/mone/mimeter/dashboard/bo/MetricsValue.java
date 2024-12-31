package run.mone.mimeter.dashboard.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Xirui Yang (yangxirui@xiaomi.com)
 * @version 1.0
 * @since 2022/7/19
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MetricsValue {

    private Long ts;

    private Double value;
}
