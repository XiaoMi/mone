
package run.mone.hive.actions.db;

import com.google.common.collect.ImmutableMap;
import run.mone.hive.actions.Action;
import run.mone.hive.common.AiTemplate;
import run.mone.hive.schema.ActionContext;
import run.mone.hive.schema.ActionReq;
import run.mone.hive.schema.Message;

public class ModifyDataAction extends Action {

    public ModifyDataAction() {
        super("ModifyData", "Modify database data based on user instructions");
        this.function = this::modifyData;
    }

    private Message modifyData(ActionReq req, Action action, ActionContext context) {
        String modificationResult = "帮我创建修改表的sql语句,thx:\n" +
                "${requirements}";

        String prompt = AiTemplate.renderTemplate(modificationResult, ImmutableMap.of("requirements", req.getMessage().getContent()));
        String res = llm(req).syncChat(getRole(), prompt);

        return Message.builder()
                .role(req.getRole().getName())
                .content(res)
                .build();
    }
}
