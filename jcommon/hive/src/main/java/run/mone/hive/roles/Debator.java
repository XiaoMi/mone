package run.mone.hive.roles;

import com.google.common.collect.Lists;
import lombok.Data;
import run.mone.hive.actions.SpeakAloud;
import run.mone.hive.actions.UserRequirement;
import run.mone.hive.llm.BaseLLM;
import run.mone.hive.schema.Message;

import java.util.HashSet;

/**
 * @author goodjava@qq.com
 * @date 2024/12/26 13:57
 */
@Data
public class Debator extends Role {

    private String opponentName;


    public Debator(String name, String profile, String opponentName, BaseLLM baseLLM) {
        this.name = name;
        this.profile = profile;
        this.opponentName = opponentName;
        this.watchList = new HashSet<>();
        this.llm = baseLLM;
        init();
    }

    @Override
    public void init() {
        super.init();
        this.setActions(Lists.newArrayList(new SpeakAloud()));
        this.watch(Lists.newArrayList(UserRequirement.class, SpeakAloud.class));
    }


    @Override
    public Message act(Message message) {
        message.setSentFrom(this.name);
        message.setSendTo(this.opponentName);
        return message;
    }
}
