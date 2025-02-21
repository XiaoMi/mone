package run.mone.moner.server.role.actions;

import run.mone.hive.actions.Action;

/**
 * @author goodjava@qq.com
 * @date 2025/2/12 13:51
 */
public class ProcessAction extends Action {
    public ProcessAction() {
        setName("ProcessAction");
        setDescription("""
                #流程TOOL
                #.当前你发现你不能解决问题的时候,你可以返回:
                <ask_followup_question>
                <question>Your question here</question>
                </ask_followup_question>
                
                #.当你发现所有任务都结束后,你必须返回:
                <attempt_completion>
                <result>
                Your final result description here
                </result>
                <command>Command to demonstrate result (optional)</command>
                </attempt_completion>
                """);
    }
}
