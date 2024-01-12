package run.mone.mimeter.dashboard.bo.sla;

import com.xiaomi.mone.http.docs.annotations.HttpApiDocClassDefine;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SlaDto implements Serializable {

    @HttpApiDocClassDefine(value = "id", required = false, description = "sla序号", defaultValue = "1")
    private Integer id;

    @HttpApiDocClassDefine(value = "name", required = true, description = "sla名称", defaultValue = "sla示例名")
    private String name;

    @HttpApiDocClassDefine(value = "note", required = false, description = "sla描述", defaultValue = "sla示理描述")
    private String note;

    @HttpApiDocClassDefine(value = "businessGroup", required = true, description = "业务分类", defaultValue = "1")
    private String businessGroup;

    @HttpApiDocClassDefine(value = "ctime", required = false, description = "创建时间", defaultValue = "0")
    private Long ctime;

    @HttpApiDocClassDefine(value = "utime", required = false, description = "更新时间", defaultValue = "1")
    private Long utime;

    @HttpApiDocClassDefine(value = "creator", required = false, description = "创建人", defaultValue = "创建人")
    private String creator;

    @HttpApiDocClassDefine(value = "updater", required = false, description = "更新人", defaultValue = "更新人")
    private String updater;

    @HttpApiDocClassDefine(value = "slaRuleList", required = false, description = "sla规则列表", defaultValue = "")
    private List<SlaRuleDto> slaRuleDtos;

    @HttpApiDocClassDefine(value = "alarmDto", required = false, description = "报警方式与接收人", defaultValue = "")
    private List<AlarmDto> alarmDtos;

}
