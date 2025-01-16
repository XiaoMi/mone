package run.mone.m78.service.agent.bo;

import lombok.AllArgsConstructor;
import run.mone.m78.service.agent.multiagent.message.SysMessage;
import run.mone.m78.service.agent.multiagent.message.SystemMessage;

/**
 * @author goodjava@qq.com
 * @date 2024/9/11 15:09
 */
@AllArgsConstructor
public class StartDiscussionMessage extends SysMessage {

    public StartDiscussionMessage(String mainPoint, String team, String topic, String summaryHint,
                                  String agent1Name, String agent2Name,
                                  String agent1Role, String agent2Role) {
        super(SystemMessage.START_DISCUSSION);
        addParameter("mainPoint", mainPoint);
        addParameter("team", team);
        addParameter("topic", topic);
        addParameter("agent1Name", agent1Name);
        addParameter("agent2Name", agent2Name);
        addParameter("agent1Role", agent1Role);
        addParameter("agent2Role", agent2Role);
        addParameter("summaryHint", summaryHint);
    }


    public StartDiscussionMessage(Discussant discussant, Discussant discussant2, String topic, String summaryHint) {
        super(SystemMessage.START_DISCUSSION);
        addParameter("mainPoint", discussant.getName() + ":" + discussant.getInitialView() + "\n" + discussant2.getName() + ":" + discussant2.getInitialView());
        addParameter("team", discussant.getName() + " " + discussant2.getName());
        addParameter("topic", topic);
        addParameter("agent1Name", discussant.getName());
        addParameter("agent2Name", discussant2.getName());
        addParameter("agent1Model", discussant.getModel());
        addParameter("agent2Model", discussant2.getModel());
        addParameter("agent1Role", discussant.getRole());
        addParameter("agent2Role", discussant2.getRole());
        addParameter("agent1Id", discussant.getBotId());
        addParameter("agent2Id", discussant2.getBotId());
        addParameter("summaryHint", summaryHint);
    }


    // 可以添加便捷的 getter 方法
    public String getMainPoint() {
        return getParameter("mainPoint");
    }

    public String getTeam() {
        return getParameter("team");
    }

    public String getTopic() {
        return getParameter("topic");
    }

    public String getAgent1Name() {
        return getParameter("agent1Name");
    }

    public String getAgent2Name() {
        return getParameter("agent2Name");
    }

    public String getAgent1Id() {
        return getParameter("agent1Id");
    }

    public String getAgent2Id() {
        return getParameter("agent2Id");
    }

    public String getAgent1Role() {
        return getParameter("agent1Role");
    }

    public String getAgent2Role() {
        return getParameter("agent2Role");
    }

    public String getAgent1Model() {
        return getParameter("agent1Model");
    }

    public String getAgent2Model() {
        return getParameter("agent2Model");
    }

    public String getSummaryHint() {
        return getParameter("summaryHint");
    }
}
