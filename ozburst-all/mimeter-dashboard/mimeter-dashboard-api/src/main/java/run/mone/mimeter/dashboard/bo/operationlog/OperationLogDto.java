package run.mone.mimeter.dashboard.bo.operationlog;

import com.xiaomi.mone.http.docs.annotations.HttpApiDocClassDefine;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class OperationLogDto implements Serializable {

    @HttpApiDocClassDefine(value = "id", required = false, description = "操作记录序号", defaultValue = "1")
    private Long id;

    @HttpApiDocClassDefine(value = "reportId", required = false, description = "报告id", defaultValue = "报告id")
    private String reportId;

    @HttpApiDocClassDefine(value = "sceneId", required = false, description = "场景id", defaultValue = "1")
    private Integer sceneId;

    @HttpApiDocClassDefine(value = "type", required = false, description = "分类", defaultValue = "1")
    private Integer type;

    @HttpApiDocClassDefine(value = "content", required = false, description = "操作内容", defaultValue = "内容")
    private String content;

    @HttpApiDocClassDefine(value = "supportOperation", required = false, description = "操作", defaultValue = "")
    private List<PerOperation> supportOperation;

    @HttpApiDocClassDefine(value = "createTime", required = false, description = "创建时间", defaultValue = "0")
    private Long createTime;

    @HttpApiDocClassDefine(value = "updateTime", required = false, description = "更新时间", defaultValue = "1")
    private Long updateTime;

    @HttpApiDocClassDefine(value = "createBy", required = false, description = "创建人", defaultValue = "创建人")
    private String createBy;

}
