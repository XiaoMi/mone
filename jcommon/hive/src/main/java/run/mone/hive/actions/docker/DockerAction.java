
package run.mone.hive.actions.docker;

import com.google.common.collect.ImmutableMap;
import run.mone.hive.actions.Action;
import run.mone.hive.common.AiTemplate;
import run.mone.hive.schema.ActionContext;
import run.mone.hive.schema.ActionReq;
import run.mone.hive.schema.Message;

public class DockerAction extends Action {

    public DockerAction() {
        super("Docker", "Execute Docker commands such as pull, push, run, etc.");
        this.function = this::executeDockerCommand;
    }

    private Message executeDockerCommand(ActionReq req, Action action, ActionContext context) {
        String dockerCommand = "请根据以下要求生成相应的Docker命令:\n" +
                "${requirements}\n\n" +
                "请返回完整的Docker命令，无需执行。如果需要多个命令，请用分号分隔。";

        String prompt = AiTemplate.renderTemplate(dockerCommand, ImmutableMap.of("requirements", req.getMessage().getContent()));
        String res = llm(req).chat(prompt);

        return Message.builder()
                .role(req.getRole().getName())
                .content("以下是根据您的要求生成的Docker命令：\n\n" + res)
                .build();
    }
}
