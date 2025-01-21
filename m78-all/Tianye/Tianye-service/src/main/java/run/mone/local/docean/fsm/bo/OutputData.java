package run.mone.local.docean.fsm.bo;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import lombok.Builder;
import lombok.Data;

/**
 * @author goodjava@qq.com
 * @date 2024/3/1 14:13
 */
@Data
@Builder
public class OutputData implements ItemData {

    private int flowId;

    private String name;

    private String referenceName;

    @Builder.Default
    private String type = "value";

    private JsonElement value;

    //string、object、array<string>、array<object>
    private String valueType;

    @Builder.Default
    private String schema = "";

    @Builder.Default
    private String desc = "";

    public JsonElement getValue() {
        return null==value?new JsonPrimitive(""):value;
    }

    public String getValueType() {
        if (valueType == null || valueType.isEmpty()){
            return "String";
        }
        return valueType;
    }

    public boolean isEmptyValue(){
        return (null==value || value.toString().equals("\"\""))?true:false;
    }
}
