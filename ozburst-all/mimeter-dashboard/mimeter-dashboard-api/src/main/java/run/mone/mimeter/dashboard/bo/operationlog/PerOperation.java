package run.mone.mimeter.dashboard.bo.operationlog;

import com.xiaomi.mone.http.docs.annotations.HttpApiDocClassDefine;
import lombok.Data;

import java.io.Serializable;

@Data
public class PerOperation  implements Serializable {

    @HttpApiDocClassDefine(value = "operationName", required = false, description = "具体操作", defaultValue = "查看报告")
    private String operationName;

    @HttpApiDocClassDefine(value = "operationType", required = false, description = "具体操作", defaultValue = "ViewReport")
    private String operationType;

    @HttpApiDocClassDefine(value = "detailInfo", required = false, description = "操作对象", defaultValue = "dsgfds")
    private String detailInfo;

    public PerOperation(String operationName, String operationType, String detailInfo) {
        this.operationName = operationName;
        this.operationType = operationType;
        this.detailInfo = detailInfo;
    }
}
