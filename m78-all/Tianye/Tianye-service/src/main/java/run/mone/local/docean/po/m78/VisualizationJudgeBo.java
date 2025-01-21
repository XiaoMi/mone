package run.mone.local.docean.po.m78;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @author HawickMason@xiaomi.com
 * @date 5/10/24 11:06 AM
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VisualizationJudgeBo implements Serializable {

    @Serial
    private static final long serialVersionUID = 2949022066964550777L;

    private String xAxisName;

    private String yAxisName;

    private List<String> xSeries;

    private List<String> ySeries;

    private String operatorName;

    private String type;

}
