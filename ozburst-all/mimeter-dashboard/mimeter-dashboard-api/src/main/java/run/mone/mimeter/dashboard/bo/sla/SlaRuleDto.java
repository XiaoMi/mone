package run.mone.mimeter.dashboard.bo.sla;

import com.xiaomi.mone.http.docs.annotations.HttpApiDocClassDefine;
import lombok.Data;

import java.io.Serializable;

@Data
public class SlaRuleDto implements Serializable {

    @HttpApiDocClassDefine(value = "id", required = false, description = "sla规则序号", defaultValue = "1")
    private Integer id;

    @HttpApiDocClassDefine(value = "slaId", required = false, description = "sla序号", defaultValue = "1")
    private Integer slaId;

    @HttpApiDocClassDefine(value = "name", required = true, description = "sla规则名称", defaultValue = "sla规则示例名")
    private String name;

    @HttpApiDocClassDefine(value = "ruleItemType", required = true, description = "指标类型", defaultValue = "业务指标")
    private String ruleItemType;

    @HttpApiDocClassDefine(value = "ruleItem", required = true, description = "指标名", defaultValue = "成功率")
    private String ruleItem;

    @HttpApiDocClassDefine(value = "condition", required = true, description = "对比条件", defaultValue = "=")
    private String condition;

    @HttpApiDocClassDefine(value = "value", required = true, description = "对比值", defaultValue = "100")
    private Integer value;

    @HttpApiDocClassDefine(value = "degree", required = true, description = "敏感程度", defaultValue = "1")
    private Integer degree;

    @HttpApiDocClassDefine(value = "action", required = true, description = "报警级别", defaultValue = "WARNING")
    private String action;

    @HttpApiDocClassDefine(value = "ctime", required = false, description = "创建时间", defaultValue = "0")
    private Long ctime;

    @HttpApiDocClassDefine(value = "utime", required = false, description = "更新时间", defaultValue = "0")
    private Long utime;

    @HttpApiDocClassDefine(value = "creator", required = false, description = "创建人", defaultValue = "创建人")
    private String creator;

    @HttpApiDocClassDefine(value = "updater", required = false, description = "更新人", defaultValue = "更新人")
    private String updater;



}
