package run.mone.mimeter.dashboard.bo.dataset;

import com.xiaomi.mone.http.docs.annotations.HttpApiDocClassDefine;
import lombok.Data;

import java.io.Serializable;

@Data
public class ParamData implements Serializable {

    @HttpApiDocClassDefine(value = "datasetId", required = true, description = "数据源id", defaultValue = "1")
    private Integer datasetId;

    @HttpApiDocClassDefine(value = "paramName", required = true, description = "参数名", defaultValue = "key1")
    private String paramName;

    @HttpApiDocClassDefine(value = "datasetName", required = true, description = "数据来源", defaultValue = "file.csv")
    private String datasetName;

    @HttpApiDocClassDefine(value = "columnIndex", required = true, description = "索引列", defaultValue = "第1列")
    private Integer columnIndex;
}
