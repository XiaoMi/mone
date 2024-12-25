
package run.mone.hive.roles;

import com.google.common.collect.Lists;
import lombok.Data;
import run.mone.hive.actions.UserRequirement;
import run.mone.hive.actions.WriteAction;
import run.mone.hive.llm.BaseLLM;
import run.mone.hive.schema.Message;

@Data
public class Writer extends Role {

    public Writer(String name, String profile, BaseLLM baseLLM) {
        this.name = name;
        this.profile = profile;
        this.llm = baseLLM;
        this.goal = "你是一名优秀的中文作家,我给你一个题目,你通过你自己的规划写出来一篇完美的文章.";
        init();
    }

    @Override
    public void init() {
        super.init();
        this.setActions(Lists.newArrayList(new WriteAction()));
        this.watch(Lists.newArrayList(UserRequirement.class, WriteAction.class));
    }

    @Override
    public Message act(Message message) {
        message.setSentFrom(this.name);
        return message;
    }
}
