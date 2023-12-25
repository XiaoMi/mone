package run.mone.mimeter.dashboard.bo.report;

import com.xiaomi.mone.http.docs.annotations.HttpApiDocClassDefine;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class RmReportReq implements Serializable {
    @HttpApiDocClassDefine(value = "reportIds", required = true)
    List<Long> reportIds;
}
