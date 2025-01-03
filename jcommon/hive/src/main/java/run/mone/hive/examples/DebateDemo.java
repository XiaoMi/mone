package run.mone.hive.examples;

import com.google.common.collect.Lists;
import run.mone.hive.Team;
import run.mone.hive.configs.LLMConfig;
import run.mone.hive.context.Context;
import run.mone.hive.llm.LLM;
import run.mone.hive.roles.Debator;

/**
 * @author goodjava@qq.com
 * @date 2024/12/26 13:55
 */
public class DebateDemo {

    //辩论
    public static void main(String[] args) {
        LLM llm = new LLM(LLMConfig.builder().debug(false).build());
        Debator biden = new Debator("Biden", "Democrat", "Trump", llm);
        Debator trump = new Debator("Trump", "Republican", "Biden", llm);

        Context context = new Context();
        context.setDefaultLLM(llm);
        Team team = new Team(context);
        team.hire(Lists.newArrayList(biden, trump));

        team.runProject("Trump: 中国是美国的朋友", "主持人", "Biden");
        team.run(3);
        System.out.println(team.getEnv().getDebugHistory());
    }

}
