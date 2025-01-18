package run.mone.m78.api.bo.flow;

import com.google.gson.JsonElement;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author wmin
 * @date 2024/3/13
 */
@Data
@Builder
public class ConditionSetting implements Serializable {

    private boolean originalInput;

    private int flowId;

    private String referenceName;

    private String name;

    //value reference
    @Builder.Default
    private String type = "reference";

    @Builder.Default
    private String valueType = "string";

    //和后边数据的操作符
    private String operator;

    private String value;

    private int flowId2;

    @Builder.Default
    private String type2 = "value";

    private String referenceName2;

    private JsonElement value2;

    //or and
    private String relationship;
}
