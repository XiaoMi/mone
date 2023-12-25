package run.mone.mimeter.dashboard.bo.report;

import com.xiaomi.mone.http.docs.annotations.HttpApiDocClassDefine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Xirui Yang (yangxirui@xiaomi.com)
 * @version 1.0
 * @since 2022/6/22
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReportInfoBo implements Serializable {

    private Long id;

    private Long sceneId;

    private String snapshotId;

    private String reportId;

    private String reportName;

    @HttpApiDocClassDefine(value = "duration", description = "压测时长(秒)")
    private Integer duration;

    @HttpApiDocClassDefine(value = "concurrency", description = "并发数")
    private Integer concurrency;

    private Integer concurrencyMax;

    @HttpApiDocClassDefine(value = "createBy", description = "创建人用户名")
    private String createBy;

    private String tenant;

    private Date createTime;

    private Date updateTime;

    private Integer status;

    private String filePath;

    @HttpApiDocClassDefine(value = "agents", description = "agents ip列表")
    private String agents;

    private String linkToDagId;

    @HttpApiDocClassDefine(value = "extra", description = "扩展信息json")
    private String extra;

    private Long finishTime;

    private String slaEventList;

    private String totalStatAnalysis;

    private String successRate;

    public boolean checkCreate() {
        return this.sceneId != null && this.sceneId > 0 &&
                StringUtils.isNotBlank(this.reportId) &&
                StringUtils.isNotBlank(this.createBy);
    }

    public boolean checkUpdate() {
        return StringUtils.isNotBlank(this.reportId);
    }

}
