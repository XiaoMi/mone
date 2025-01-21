package run.mone.local.docean.po.m78;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author HawickMason@xiaomi.com
 * @date 4/23/24 19:53
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SimpleBotFlowBo implements Serializable {

    private Long id;

    private String name;

    private String desc;

}
