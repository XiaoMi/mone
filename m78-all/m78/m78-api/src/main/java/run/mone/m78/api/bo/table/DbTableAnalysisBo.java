package run.mone.m78.api.bo.table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author HawickMason@xiaomi.com
 * @date 4/28/24 14:32
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class DbTableAnalysisBo {

    @Builder.Default
    private boolean hit = false;

    @Builder.Default
    private String sqlType = "";

    @Builder.Default
    private String sql = "";

    @Builder.Default
    private String tableExtra = "";

    @Builder.Default
    private Integer tableType = 0;

    private Long connectionId;

    // 当前取值: "", 柱装图
    private String drawType;

    private String xAxisName;

    private String yAxisName;
}
