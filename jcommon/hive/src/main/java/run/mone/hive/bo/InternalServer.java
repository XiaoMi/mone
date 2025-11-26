package run.mone.hive.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author goodjava@qq.com
 * @date 2025/4/29 14:07
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InternalServer {

    private String name;

    private String args;

}
