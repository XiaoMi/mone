package run.mone.hive.roles;

import lombok.extern.slf4j.Slf4j;
import run.mone.hive.actions.AnalyzeArchitecture;
import run.mone.hive.schema.Message;

import java.util.Collections;


/**
 * @author goodjava@qq.com
 * 架构师 (分析项目)
 */
@Slf4j
public class Architect extends Role {

    public Architect() {
        super("Architect", "Architect", "design a concise, usable, complete software system", "make sure the architecture is simple enough and use appropriate open source \" +\n" +
                "            \"libraries. Use same language as user requirement");
        setActions(Collections.singletonList(new AnalyzeArchitecture()));
    }

    @Override
    public Message processMessage(Message message) {
        message.setSendTo("Design");
        return message;
    }
}