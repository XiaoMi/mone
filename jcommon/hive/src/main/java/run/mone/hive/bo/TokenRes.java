package run.mone.hive.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author goodjava@qq.com
 * @date 2025/6/18 15:16
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TokenRes {

    private boolean success;

    private String userId;


}
