package run.mone.local.docean.fsm.bo;

import com.google.gson.JsonElement;

/**
 * @author goodjava@qq.com
 * @date 2024/3/1 14:32
 */
public interface ItemData {

    String getName();

    String getReferenceName();

    String getType();

    String getValueType();

    JsonElement getValue();

    int getFlowId();
}
