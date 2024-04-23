package run.mone.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author goodjava@qq.com
 * @date 2024/4/19 22:36
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Page<T> {

    private List<T> content;
    private int page;
    private int size;
    private long total;

}
