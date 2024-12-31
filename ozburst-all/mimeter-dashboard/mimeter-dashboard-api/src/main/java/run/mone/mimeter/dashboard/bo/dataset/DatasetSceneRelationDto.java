package run.mone.mimeter.dashboard.bo.dataset;

import com.xiaomi.mone.http.docs.annotations.HttpApiDocClassDefine;
import lombok.Data;

import java.io.Serializable;
import java.util.Map;

@Data
public class DatasetSceneRelationDto implements Serializable {

    @HttpApiDocClassDefine(value = "id", required = false, description = "序号", defaultValue = "1")
    private Integer id;

    @HttpApiDocClassDefine(value = "datasetId", required = true, description = "数据源序号", defaultValue = "1")
    private Integer datasetId;

    @HttpApiDocClassDefine(value = "sceneId", required = true, description = "场景序号", defaultValue = "1")
    private Integer sceneId;

    @HttpApiDocClassDefine(value = "ctime", required = false, description = "创建时间", defaultValue = "0")
    private Long ctime;

    @HttpApiDocClassDefine(value = "utime", required = false, description = "更新时间", defaultValue = "1")
    private Long utime;

    @HttpApiDocClassDefine(value = "creator", required = false, description = "创建人", defaultValue = "创建人")
    private String creator;

    @HttpApiDocClassDefine(value = "updater", required = false, description = "更新人", defaultValue = "更新人")
    private String updater;



}
