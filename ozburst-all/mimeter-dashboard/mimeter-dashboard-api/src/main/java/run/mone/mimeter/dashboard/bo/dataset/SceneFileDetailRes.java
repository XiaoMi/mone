package run.mone.mimeter.dashboard.bo.dataset;

import com.xiaomi.mone.http.docs.annotations.HttpApiDocClassDefine;
import lombok.Data;

import java.util.List;

/**
 * @author dingpei
 * @date 2022-07-15
 */
@Data
public class SceneFileDetailRes {

    @HttpApiDocClassDefine(value = "datasetLists",required = true,description = "文件列表")
    List<DatasetDto> datasetLists;

    @HttpApiDocClassDefine(value = "paramDataList",required = true,description = "参数列表")
    List<ParamData> paramDataList;


}
