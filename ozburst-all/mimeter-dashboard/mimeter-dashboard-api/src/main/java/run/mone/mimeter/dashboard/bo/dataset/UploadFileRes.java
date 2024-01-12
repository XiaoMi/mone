package run.mone.mimeter.dashboard.bo.dataset;

import com.xiaomi.mone.http.docs.annotations.HttpApiDocClassDefine;
import lombok.Data;

import java.util.List;

/**
 * @author dingpei
 * @date 2022-07-15
 */
@Data
public class UploadFileRes {

    @HttpApiDocClassDefine(value = "fileName", required = false, description = "文件名", defaultValue = "tmp.csv")
    private String fileName;

    @HttpApiDocClassDefine(value = "fileUrl", required = false, description = "文件下载链接", defaultValue = "http://")
    private String fileUrl;

    @HttpApiDocClassDefine(value = "fileKsKey", required = false, description = "文件位于云上的key", defaultValue = "xx/xxx/xx")
    private String fileKsKey;

    @HttpApiDocClassDefine(value = "fileRows", required = false, description = "文件行数", defaultValue = "10")
    private Long fileRows;

    @HttpApiDocClassDefine(value = "fileColumns", required = false, description = "文件列数", defaultValue = "3")
    private int fileColumns;

    @HttpApiDocClassDefine(value = "fileSize", required = false, description = "文件大小", defaultValue = "100")
    private Long fileSize;

    @HttpApiDocClassDefine(value = "firstRow", required = false, description = "首行", defaultValue = "")
    private List<String> firstRow;

    @HttpApiDocClassDefine(value = "previewFileRows", required = false, description = "文件预览", defaultValue = "")
    private List<String> previewFileRows;


}
