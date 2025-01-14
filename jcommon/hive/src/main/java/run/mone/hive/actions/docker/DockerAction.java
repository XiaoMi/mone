
package run.mone.hive.actions.docker;

import com.google.common.collect.ImmutableMap;
import run.mone.hive.actions.Action;
import run.mone.hive.common.AiTemplate;
import run.mone.hive.common.Prompts;
import run.mone.hive.schema.ActionContext;
import run.mone.hive.schema.ActionReq;
import run.mone.hive.schema.Message;
import run.mone.hive.utils.XmlParser;

public class DockerAction extends Action {

    public DockerAction() {
        super("Docker", "Execute Docker commands such as pull, push, run, etc.");
        this.function = this::executeDockerCommand;
    }

    private Message executeDockerCommand(ActionReq req, Action action, ActionContext context) {
        String dockerCommand = "请根据以下要求生成相应的Docker命令:\n" +
                "${requirements}\n\n" +
                "请返回完整的Docker命令，无需执行。如果需要多个命令，请用分号分隔。\n" +
                Prompts.PROMPT_FORMAT;

        String prompt = AiTemplate.renderTemplate(dockerCommand, ImmutableMap.of("requirements", req.getMessage().getContent()));
        String res = llm(req).chat(prompt);
        res = XmlParser.trimString(XmlParser.parser(res).get(0));
        return Message.builder()
                .role(req.getRole().getName())
                .content(res)
                .build();
    }
}
