
package run.mone.hive.actions.common;

import com.google.common.collect.ImmutableMap;
import run.mone.hive.actions.Action;
import run.mone.hive.common.AiTemplate;
import run.mone.hive.common.Prompts;
import run.mone.hive.schema.ActionContext;
import run.mone.hive.schema.ActionReq;
import run.mone.hive.schema.Message;
import run.mone.hive.roles.Role;
import run.mone.hive.utils.XmlParser;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DecideNextSpeakerAction extends Action {

    public DecideNextSpeakerAction() {
        super("DecideNextSpeaker", "Decide which role should speak next based on the current context");
        this.function = this::decideNextSpeaker;
    }

    private Message decideNextSpeaker(ActionReq req, Action action, ActionContext context) {
        List<Role> availableRoles = new ArrayList<>(req.getEnv().getRoles().values());
        String currentContext = req.getHistory().stream().map(it -> it.getRole() + ":" + it.getContent()).collect(Collectors.joining("\n"));

        String decisionPrompt = "Based on the current context and available roles, decide which role should speak next:\n" +
                "你只需要返回角色名字即可\n" +
                "Current context: ${currentContext}\n" +
                "Available roles: \n${availableRoles}\n" +
                "Provide the name of the role that should speak next and a brief explanation for your decision.\n" +
                Prompts.PROMPT_FORMAT

                ;

        String prompt = AiTemplate.renderTemplate(decisionPrompt, ImmutableMap.of(
                "currentContext", currentContext,
                "availableRoles", availableRoles.stream().map(it -> "name:" + it.getName() + " profile:(" + it.getProfile() + ")").collect(Collectors.joining("\n"))
        ));

        String decision = llm(req).chat(prompt);

        String res = XmlParser.trimString(XmlParser.parser(decision).get(0));

        return Message.builder()
                .role(req.getRole().getName())
                .content(res)
                .build();
    }
}
