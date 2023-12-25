package run.mone.mimeter.dashboard.bo.dataset;

import com.xiaomi.mone.http.docs.annotations.HttpApiDocClassDefine;
import lombok.Data;

import java.util.List;

/**
 * @author dingpei
 * @date 2022-07-15
 */
@Data
public class PreviewFileRes {

    @HttpApiDocClassDefine(value = "fileName", required = false, description = "文件名", defaultValue = "tmp.csv")
    private String fileName;

    @HttpApiDocClassDefine(value = "fileRows", required = false, description = "文件行数", defaultValue = "10")
    private Long fileRows;

    @HttpApiDocClassDefine(value = "previewFileRows", required = false, description = "文件预览", defaultValue = "")
    private List<String> previewFileRows;


}
