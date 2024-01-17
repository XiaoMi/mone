package run.mone.mimeter.dashboard.bo.report;

import com.xiaomi.mone.http.docs.annotations.HttpApiDocClassDefine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Xirui Yang (yangxirui@xiaomi.com)
 * @version 1.0
 * @since 2022/8/15
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchApiLogReq {

    @HttpApiDocClassDefine(value = "sceneId")
    private Long sceneId;

    @HttpApiDocClassDefine(value = "taskId", required = true)
    private Long taskId;

    @HttpApiDocClassDefine(value = "serialId", description = "链路id")
    private Long serialId;

    @HttpApiDocClassDefine(value = "reportId", description = "报告id")
    private String reportId;

    @HttpApiDocClassDefine(value = "apiId", description = "api id")
    private Long apiId;

    @HttpApiDocClassDefine(value = "apiUri", description = "http url或dubbo服务名")
    private String apiUri;

    @HttpApiDocClassDefine(value = "apiMethod", description = "http或dubbo方法")
    private String apiMethod;

    @HttpApiDocClassDefine(value = "rtMin", description = "最小rt")
    private Long rtMin;

    @HttpApiDocClassDefine(value = "rtMax", description = "最大rt")
    private Long rtMax;

    @HttpApiDocClassDefine(value = "failed", description = "是否失败")
    private Boolean failed;

    @HttpApiDocClassDefine(value = "respCode", description = "响应状态码")
    private Integer respCode;

    @HttpApiDocClassDefine(value = "startTs", description = "开始时间戳")
    private Long startTs;

    @HttpApiDocClassDefine(value = "endTs", description = "结束时间戳")
    private Long endTs;

    @HttpApiDocClassDefine(value = "traceId", description = "traceId")
    private String traceId;

    @HttpApiDocClassDefine(value = "pageNo", required = true)
    private Integer pageNo;

    @HttpApiDocClassDefine(value = "pageSize", required = true)
    private Integer pageSize;
}
