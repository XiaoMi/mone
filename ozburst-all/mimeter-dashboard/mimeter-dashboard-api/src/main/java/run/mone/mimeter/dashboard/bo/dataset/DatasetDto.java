package run.mone.mimeter.dashboard.bo.dataset;

import com.xiaomi.mone.http.docs.annotations.HttpApiDocClassDefine;
import lombok.Data;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@Data
public class DatasetDto implements Serializable {

    @HttpApiDocClassDefine(value = "id", required = false, description = "数据源序号", defaultValue = "1")
    private Integer id;

    @HttpApiDocClassDefine(value = "name", required = true, description = "数据源名称", defaultValue = "数据源示例名")
    private String name;

    @HttpApiDocClassDefine(value = "note", required = false, description = "数据源描述", defaultValue = "数据源示理描述")
    private String note;

    @HttpApiDocClassDefine(value = "type", required = true, description = "数据源分类", defaultValue = "1")
    private Integer type;

    @HttpApiDocClassDefine(value = "defaultParamName", required = false, description = "默认参数名", defaultValue = "paramName")
    private String defaultParamName;

    @HttpApiDocClassDefine(value = "ignoreFirstRow", required = false, description = "忽略上传文件首行", defaultValue = "0")
    private Integer ignoreFirstRow;

    @HttpApiDocClassDefine(value = "fileName", required = false, description = "文件名", defaultValue = "tmp.csv")
    private String fileName;

    @HttpApiDocClassDefine(value = "fileUrl", required = false, description = "文件下载链接", defaultValue = "http://")
    private String fileUrl;

    @HttpApiDocClassDefine(value = "fileKsKey", required = false, description = "文件位于云上的key", defaultValue = "xx/xxx/xx")
    private String fileKsKey;

    @HttpApiDocClassDefine(value = "fileRows", required = false, description = "文件行数", defaultValue = "10")
    private Long fileRows;

    @HttpApiDocClassDefine(value = "fileSize", required = false, description = "文件大小", defaultValue = "100")
    private Long fileSize;

    @HttpApiDocClassDefine(value = "previewFileRows", required = false, description = "预览文件", defaultValue = "")
    private List<String> previewFileRows;

    @HttpApiDocClassDefine(value = "header", required = true, description = "请求header", defaultValue = "{\"X-Real-IP\":\"127.0.0.1\"}")
    private Map<String, String> header;

    @HttpApiDocClassDefine(value = "interfaceUrl", required = true, description = "接口url", defaultValue = "http://")
    private String interfaceUrl;

    @HttpApiDocClassDefine(value = "trafficRecordId", required = false, description = "流量录制", defaultValue = "100")
    private Integer trafficRecordId;

    @HttpApiDocClassDefine(value = "bindScenes", required = false, description = "引用场景", defaultValue = "")
    private Map<Integer, String> bindScenes;

    @HttpApiDocClassDefine(value = "ctime", required = false, description = "创建时间", defaultValue = "0")
    private Long ctime;

    @HttpApiDocClassDefine(value = "utime", required = false, description = "更新时间", defaultValue = "1")
    private Long utime;

    @HttpApiDocClassDefine(value = "creator", required = false, description = "创建人", defaultValue = "创建人")
    private String creator;

    @HttpApiDocClassDefine(value = "updater", required = false, description = "更新人", defaultValue = "更新人")
    private String updater;

    @HttpApiDocClassDefine(value = "tenant",ignore = true, description = "租户", defaultValue = "")
    private String tenant;

}
