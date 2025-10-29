package run.mone.moner.server.role.tool;

import run.mone.hive.roles.tool.ITool;

public class MemoryActionTool implements ITool {

    @Override
    public String getName() {
        return "MemoryAction";
    }

    @Override
    public String description() {
        return "记忆工具：用于查询、保存和管理长期记忆（search/save/reset/get_all）。当需要检索历史信息、保存新知识或管理上下文时使用。";
    }

    @Override
    public String parameters() {
        return """
                - action: (required) One of [search, save, reset, get_all]
                - query: (required for search) The search query
                - content: (required for save) Content to save
                - max_results: (optional) Max results (default 5)
                - threshold: (optional) Similarity threshold (0.0-1.0, default 0.7)
                - metadata: (optional for save) Extra metadata as JSON
                """;
    }

    @Override
    public String usage() {
        return """
                (Attention: Return within <memory> tags when using this tool)
                Example: Save info
                <memory>
                  <action>save</action>
                  <content>用户喜欢Python做数据分析</content>
                  <metadata>{"category":"preference","domain":"programming"}</metadata>
                </memory>
                """;
    }
}

