package run.mone.m78.service.agent.multiagent.message;

/**
 * @author goodjava@qq.com
 * @date 2024/9/6 18:03
 */
public class InitialView implements Message{
    public final String agentName;
    public final String view;
    public InitialView(String agentName, String view) {
        this.agentName = agentName;
        this.view = view;
    }
}
