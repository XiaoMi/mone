package run.mone.hive.roles;

import run.mone.hive.actions.WriteDesign;
import run.mone.hive.schema.Message;

import java.util.Collections;

/**
 * @author goodjava@qq.com
 * 设计师 (确定创建或者修改那些类)
 */
public class Design extends Role{


    public Design() {
        super("Design", "Design", "", "");
        setActions(Collections.singletonList(new WriteDesign()));
    }

    @Override
    public Message processMessage(Message message) {
        message.setSendTo("Engineer");
        return super.processMessage(message);
    }
}
