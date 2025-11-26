package run.mone.hive.bo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @author goodjava@qq.com
 * @date 2025/6/18 15:16
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TokenReq {

    private String userId;

    private Map<String, Object> arguments;

}
