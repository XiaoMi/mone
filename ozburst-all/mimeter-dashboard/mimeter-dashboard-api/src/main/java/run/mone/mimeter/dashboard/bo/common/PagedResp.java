package run.mone.mimeter.dashboard.bo.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Xirui Yang (yangxirui@xiaomi.com)
 * @version 1.0
 * @since 2022/7/7
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PagedResp<T> {

    private long total;

    private T data;
}
