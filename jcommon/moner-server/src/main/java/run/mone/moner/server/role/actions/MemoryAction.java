package run.mone.moner.server.role.actions;

import java.util.concurrent.CountDownLatch;

import com.google.gson.JsonObject;

import run.mone.hive.actions.Action;
import run.mone.hive.roles.ReactorRole;
import run.mone.hive.roles.tool.MemoryTool;
import run.mone.hive.schema.Message;
import run.mone.moner.server.common.GsonUtils;
import run.mone.moner.server.common.MultiXmlParser;
import run.mone.moner.server.common.Result;
import run.mone.moner.server.context.ApplicationContextProvider;

public class MemoryAction extends Action {

    public MemoryAction() {
        String memoryActionPrompt = "";
        MemoryTool memoryTool = ApplicationContextProvider.getBean(MemoryTool.class);
        if (memoryTool != null) {
            memoryActionPrompt = "\n\n#.记忆工具定义:\n" + memoryTool.description() 
                + "\n记忆工具参数:\n" + memoryTool.parameters()
                + "\n记忆工具使用示例:\n" + memoryTool.usage()
                + "\n如果你在在chat工具中返回对本工具的使用，则要参考使用示例，返回xml格式在chat的内层";
        }
        setName(memoryTool.getName());
        setDescription(memoryActionPrompt);
        setFunction((req, action, ctx) -> {
            Message reqMsg = req.getMessage();
            Result data = (Result) reqMsg.getData();
            String actions = data.getKeyValuePairs().get("arguments");
            // 执行记忆存储
            ReactorRole role = new ReactorRole("dummy", new CountDownLatch(1), null);
            role.setOwner("chrome");
            memoryTool.execute(role, GsonUtils.gson.fromJson(reqMsg.getContent(), JsonObject.class));
            String xml = new MultiXmlParser().jsonToXml(actions);
            return Message.builder().content(xml).build();
        });
    }
}
