package run.mone.m78.service.agent.state;

/**
 * @author goodjava@qq.com
 * @date 2023/12/4 17:59
 */
public enum ActionType {
    //记忆到内存
    memary,
    //仅仅打印输出
    print,
    //执行方法
    method,
    //直接退出了,不再操作了
    exit,
    //跳过这次执行的function
    skip,
}
