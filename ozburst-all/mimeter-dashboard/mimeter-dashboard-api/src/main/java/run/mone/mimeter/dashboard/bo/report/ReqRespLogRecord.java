package run.mone.mimeter.dashboard.bo.report;

import com.xiaomi.mone.http.docs.annotations.HttpApiDocClassDefine;
import lombok.Builder;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * @author Xirui Yang (yangxirui@xiaomi.com)
 * @version 1.0
 * @since 2022/8/15
 */
@Data
@Builder
public class ReqRespLogRecord {

    private Long timestamp;

    private boolean failed;

    private Long taskId;

    @HttpApiDocClassDefine(value = "apiType", required = true, description = "api类型，http:1, dubbo:3")
    private Integer apiType;

    @HttpApiDocClassDefine(value = "uri", required = true, description = "http url或dubbo服务名")
    private String uri;

    @HttpApiDocClassDefine(value = "method", required = true, description = "http或dubbo方法")
    private String method;

    private Integer rt;

    @HttpApiDocClassDefine(value = "respCode", description = "响应状态码")
    private Long code;

    private Long sceneId;

    @HttpApiDocClassDefine(value = "serialId", description = "链路id")
    private Long serialId;

    @HttpApiDocClassDefine(value = "reportId", description = "报告id")
    private String reportId;

    @HttpApiDocClassDefine(value = "apiId", description = "api id")
    private Long apiId;

    @HttpApiDocClassDefine(value = "traceId", description = "traceId")
    private String traceId;

    @HttpApiDocClassDefine(value = "params", description = "请求body")
    private String params;

    @HttpApiDocClassDefine(value = "result", description = "返回结果")
    private String result;

    private String errorInfo;

    private String reqHeaders;

    private String respHeaders;

    public boolean validate() {
        return this.taskId != null && this.taskId > 0 &&
                StringUtils.isNotBlank(this.uri) && StringUtils.isNotBlank(this.method) &&
                StringUtils.isNotBlank(this.method) && this.rt != null && this.rt > 0 &&
                this.code != null && this.sceneId != null && this.sceneId > 0 &&
                this.result != null && StringUtils.isNotBlank(this.reportId);
    }
}
