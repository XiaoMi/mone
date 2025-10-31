package run.mone.moner.server.role;

import run.mone.hive.roles.Role;

/**
 * @author goodjava@qq.com
 * @date 2025/2/11 16:37
 */
public class Miliner extends Role {

    public Miliner() {
        super("Miliner_Chrome(项目部署者)","项目部署者");
        this.goal = """
                如果用户需求描述中包含部署相关的关键词，例如"部署"、"发布"、"上线"等，请使用项目部署相关的工具，注意只允许操作env是staging的情况，如果是online则拒绝执行。
                以下是具体的项目部署工具和步骤：步骤(工具名字)
                1.创建部署平台tab(这步不能省略, 必须首先执行)(OpenTabAction)<url:https://xxx>
                2.查看【应用切换】位置是否是我的-指定的项目名【如我的-dubbo3-test】，注意在第二个input框中先focus然后在搜索(OperationAction)<next:"true">
                3.查看第2步中搜索出的列表，点击应用列表中和指定项目名匹配的元素【如点击dubbo3-test元素】(OperationAction)
                4.寻找应用部署下匹配的环境名称条目（如果用户没有指定要求用户指定部署环境的名称），点击该部署环境名称对应的部署按钮【播放图标】，会弹出部署框(OperationAction)<next:"true">
                5.在弹出框中点击【运行】按钮(OperationAction)
                6.查看页面，部署开始后调用结束工具(attempt_completion)

                如果发现去的页面并不匹配或者步骤出错,结束即可(attempt_completion)
                """;
    }
}