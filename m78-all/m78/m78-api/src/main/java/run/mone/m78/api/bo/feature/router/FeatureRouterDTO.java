package run.mone.m78.api.bo.feature.router;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

/**
 * @author HawickMason@xiaomi.com
 * @date 2/6/24 15:28
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class FeatureRouterDTO implements Serializable {

    private static final long serialVersionUID = -8622719959957696877L;

    private long id;

    private String name;

    private String userName;

    private Long labelId;

    private Integer status;

    private String createTime;

    private String updateTime;

    private Map<String, String> routerMeta;

    private String curl;

    private String content;

    private Integer type;

}
