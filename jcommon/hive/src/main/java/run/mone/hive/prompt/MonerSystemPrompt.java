package run.mone.hive.prompt;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;
import run.mone.hive.bo.InternalServer;
import run.mone.hive.bo.AgentMarkdownDocument;
import run.mone.hive.common.AiTemplate;
import run.mone.hive.common.Constants;
import run.mone.hive.common.GsonUtils;
import run.mone.hive.common.Safe;
import run.mone.hive.common.function.DefaultValueFunction;
import run.mone.hive.common.function.InvokeMethodFunction;
import run.mone.hive.mcp.hub.McpHub;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.hive.roles.ReactorRole;
import run.mone.hive.roles.tool.ITool;
import run.mone.hive.schema.Message;
import run.mone.hive.utils.CacheService;
import run.mone.hive.utils.FileUtils;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author goodjava@qq.com
 * @date 2024/12/17 18:14
 */
@Slf4j
public class MonerSystemPrompt {

    // 获取系统名字(class)
    public static String getSystemName() {
        return System.getProperty("os.name");
    }

    // 获取默认shell name (class)
    public static String getDefaultShellName() {
        return System.getProperty("os.name").toLowerCase().contains("win") ? "cmd.exe" : "bash";
    }

    // 获取home 的 dir(class)
    public static String getHomeDir() {
        return System.getProperty("user.home");
    }

    //当前工作目录
    public static String cwd(ReactorRole role) {
        if (role.getRoleConfig().containsKey(Constants.WORKSPACE_PATH)) {
            return role.getRoleConfig().get(Constants.WORKSPACE_PATH);
        }
        return role.getRoleConfig().getOrDefault("cwd", getHomeDir());
    }

    public static String hiveCwd(ReactorRole role) {
        String workspacePath = cwd(role);
        return workspacePath
                + (workspacePath.endsWith(File.separator) ? "" : File.separator)
                + ".hive";
    }

    /**
     * 获取自定义指令
     * <p>
     * 首先尝试从工作目录下的.hive/agent.md文件读取自定义指令
     * 如果文件不存在或读取失败，则从角色配置中获取
     *
     * @param role               反应堆角色
     * @param customInstructions 默认指令（如果文件不存在且配置中无指令时使用）
     * @return 自定义指令内容
     */
    public static String customInstructions(ReactorRole role, String customInstructions) {
        String workspacePath = cwd(role);
        if (StringUtils.isBlank(workspacePath)) {
            log.warn("工作空间路径为空，使用默认指令");
            return role.getRoleConfig().getOrDefault("customInstructions", customInstructions);
        }

        // 构建.hive/agent.md文件路径
        String mdStr = getAllMdFiles(workspacePath);
        if (mdStr != null) return mdStr;

        // 从角色配置中获取自定义指令，如果不存在则使用默认指令
        return role.getRoleConfig().getOrDefault("customInstructions", customInstructions);
    }

    /**
     * 获取.hive目录下所有md文件的内容并拼接
     *
     * @param workspacePath 工作空间路径
     * @return 拼接后的md内容，如果没有找到任何md文件则返回null
     */
    @Nullable
    private static String getAllMdFiles(String workspacePath) {
        String hiveDir = workspacePath
                + (workspacePath.endsWith(File.separator) ? "" : File.separator)
                + ".hive";

        File hiveDirFile = new File(hiveDir);
        if (!hiveDirFile.exists() || !hiveDirFile.isDirectory()) {
            log.debug(".hive目录不存在: {}", hiveDir);
            return null;
        }

        // 获取所有.md文件
        File[] mdFiles = hiveDirFile.listFiles((dir, name) -> name.toLowerCase().endsWith(".md"));
        if (mdFiles == null || mdFiles.length == 0) {
            log.debug(".hive目录下没有找到md文件: {}", hiveDir);
            return null;
        }

        StringBuilder result = new StringBuilder();
        boolean hasContent = false;

        // 按文件名排序，确保输出顺序一致
        Arrays.sort(mdFiles, (f1, f2) -> f1.getName().compareTo(f2.getName()));

        for (File mdFile : mdFiles) {
            try {
                String content = FileUtils.readMarkdownFile(mdFile.getAbsolutePath());
                if (StringUtils.isNotBlank(content)) {
                    if (hasContent) {
                        result.append("\n\n");
                    }
                    result.append(content);
                    hasContent = true;
                    log.debug("成功读取md文件: {}", mdFile.getName());
                }
            } catch (Exception e) {
                log.debug("读取md文件失败: {}, 原因: {}", mdFile.getName(), e.getMessage());
            }
        }

        return hasContent ? result.toString() : null;
    }

    @Nullable
    private static String getAgentMd(String workspacePath) {
        String filePath = workspacePath
                + (workspacePath.endsWith(File.separator) ? "" : File.separator)
                + ".hive" + File.separator + "agent.md";

        try {
            // 尝试读取文件内容
            String mdStr = FileUtils.readMarkdownFile(filePath);
            if (StringUtils.isNotBlank(mdStr)) {
                log.debug("成功从{}读取自定义指令", filePath);
                return mdStr;
            }
        } catch (Exception e) {
            log.debug("无法读取自定义指令文件: {}, 原因: {}", filePath, e.getMessage());
        }
        return null;
    }


    // 为了向后兼容，提供不带enableTaskProgress参数的重载方法
    public static String mcpPrompt(Message message, ReactorRole role, String roleDescription, String from, String name, String customInstructions, List<ITool> tools, List<McpSchema.Tool> mcpTools, String workFlow) {
        return mcpPrompt(message, role, roleDescription, from, name, customInstructions, tools, mcpTools, workFlow, false);
    }

    public static String mcpPrompt(Message message, ReactorRole role, String roleDescription, String from, String name, String customInstructions, List<ITool> tools, List<McpSchema.Tool> mcpTools, String workFlow, boolean enableTaskProgress) {
        Map<String, Object> data = new HashMap<>();
        data.put("tool_use_info", MonerSystemPrompt.TOOL_USE_INFO);
        data.put("config", "");
        data.put("name", name);
        data.put("osName", MonerSystemPrompt.getSystemName());
        data.put("defaultShell", MonerSystemPrompt.getDefaultShellName());
        data.put("homeDir", MonerSystemPrompt.getHomeDir());
        data.put("cwd", MonerSystemPrompt.cwd(role));
        data.put("hiveCwd", MonerSystemPrompt.hiveCwd(role));
        data.put("customInstructions", MonerSystemPrompt.customInstructions(role, customInstructions));
        data.put("roleDescription", roleDescription);
        data.put("enableTaskProgress", enableTaskProgress);
        List<Map<String, Object>> serverList = getMcpInfo(from, role);
        data.put("serverList", serverList);
        if (StringUtils.isEmpty(workFlow)) {
            workFlow = "";
        }
        data.put("workflow", workFlow);

        //注入工具
        data.put("toolList", tools);
        //注入mcp工具
        data.put("internalServer", InternalServer.builder().name("internalServer").args("").build());
        data.put("mcpToolList", mcpTools.stream().filter(it -> !it.name().endsWith("_chat")).collect(Collectors.toList()));

        //markdown文件会根本上重置这些配置
        if (null != message.getData() && message.getData() instanceof AgentMarkdownDocument md) {
            String rd = """
                    \n
                    profile: %s
                    goal: %s
                    constraints: %s
                    \n
                    """.formatted(md.getProfile(), md.getGoal(), md.getConstraints());

            data.put("name", md.getName());
            data.put("roleDescription", rd);
            data.put("customInstructions",md.getAgentPrompt());
        }

        return AiTemplate.renderTemplate(MonerSystemPrompt.MCP_PROMPT, data,
                Lists.newArrayList(
                        //反射执行
                        Pair.of("invoke", new InvokeMethodFunction()),
                        //可以使用默认值
                        Pair.of("value", new DefaultValueFunction())
                ));
    }

    //获取mcp的信息(主要是tool的信息)
    public static List<Map<String, Object>> getMcpInfo(String from, ReactorRole role) {
        final List<Map<String, Object>> serverList = new ArrayList<>();
        List<Map<String, Object>> sl = (List<Map<String, Object>>) CacheService.ins().getObject(CacheService.tools_key);
        if (null != sl) {
            serverList.addAll(sl);
        } else {
            McpHub mcpHub = role.getMcpHub();
            if (mcpHub == null) {
                return serverList;
            }
            mcpHub.getConnections().forEach((key, value) -> Safe.run(() -> {
                Map<String, Object> server = new HashMap<>();
                server.put("name", key);
                server.put("args", "");
                server.put("connection", value);

                McpSchema.ListToolsResult tools = value.getClient().listTools();
                String toolsStr = tools
                        .tools().stream().map(t -> "name:" + t.name() + "\n" + "descrip tion:" + t.description() + "\n"
                                + "inputSchema:" + GsonUtils.gson.toJson(t.inputSchema()))
                        .collect(Collectors.joining("\n\n"));
                server.put("tools", toolsStr);

                serverList.add(server);
            }));
            if (!serverList.isEmpty()) {
                CacheService.ins().cacheObject(CacheService.tools_key, serverList);
            }
        }
        return serverList;
    }

    public static final String TOOL_USE_INFO = """
            You have access to a set of tools that are executed upon the user's approval. You can use one tool per message, and will receive the result of that tool use in the user's response. You use tools step-by-step to accomplish a given task, with each tool use informed by the result of the previous tool use.
            (任何工具每次只使用一个,不要一次返回多个工具,不管是mcp tool 还是 tool,你必须严格遵守这个条款,不然系统会崩溃 thx)
            """;


    // mcp 调用的会使用这个prompt
    public static final String MCP_PROMPT = """
            You are ${name}, ${value(roleDescription,' a highly skilled software engineer with extensive knowledge in many programming languages, frameworks, design patterns, and best practices.')}
            
            ====
            
            You are very good at using tools, these are some rules for using tools and the tools you can use.
            
            
            TOOL USE
            
            ${tool_use_info}
            
            # Tool Use Formatting
            
            Tool use is formatted using XML-style tags. The tool name is enclosed in opening and closing tags, and each parameter is similarly enclosed within its own set of tags. Here's the structure:
            
            <tool_name>
            <parameter1_name>value1</parameter1_name>
            <parameter2_name>value2</parameter2_name>
            ...
            </tool_name>
            
            
            
            ## use_mcp_tool
            Description: Request to use a tool provided by a connected MCP server. Each MCP server can provide multiple tools with different capabilities. Tools have defined input schemas that specify required and optional parameters.
            Parameters:
            - server_name: (required) The name of the MCP server providing the tool
            - tool_name: (required) The name of the tool to execute
            - arguments: (required) A JSON object containing the tool's input parameters, following the tool's input schema
            <% if(enableTaskProgress) { %>- task_progress: (optional) A checklist showing task progress after this tool use is completed. (See 'Updating Task Progress' section for more details)<% } %>
            Usage:
            <use_mcp_tool>
            <server_name>server name here</server_name>
            <tool_name>tool name here</tool_name>
            <arguments>
            {
              "param1": "value1",
              "param2": "value2"
            }
            </arguments>
            <% if(enableTaskProgress) { %><task_progress>
            Checklist here (optional)
            </task_progress><% } %>
            </use_mcp_tool>
            
            # Tool Use Examples
            
            ## Example 1: Requesting to execute a command
            
            <execute_command>
            <command>npm run dev</command>
            <requires_approval>false</requires_approval>
            <% if(enableTaskProgress) { %><task_progress>
            - [x] Set up project structure
            - [x] Install dependencies
            - [ ] Run command to start server
            - [ ] Test application
            </task_progress><% } %>
            </execute_command>
            
            ## Example 2: Requesting to use an MCP tool
            
            <use_mcp_tool>
            <server_name>weather-server</server_name>
            <tool_name>get_forecast</tool_name>
            <arguments>
            {
              "city": "San Francisco",
              "days": 5
            }
            </arguments>
            <% if(enableTaskProgress) { %><task_progress>
            - [x] Set up project structure
            - [x] Install dependencies  
            - [ ] Get weather data
            - [ ] Test application
            </task_progress><% } %>
            </use_mcp_tool>
            
            ## execute_command
            Description: Request to execute a CLI command on the system. Use this when you need to perform system operations or run specific commands to accomplish any step in the user's task. You must tailor your command to the user's system and provide a clear explanation of what the command does. For command chaining, use the appropriate chaining syntax for the user's shell. Prefer to execute complex CLI commands over creating executable scripts, as they are more flexible and easier to run. Commands will be executed in the current working directory: ${cwd}
            Parameters:
            - command: (required) The CLI command to execute. This should be valid for the current operating system. Ensure the command is properly formatted and does not contain any harmful instructions.
            - requires_approval: (required) A boolean indicating whether this command requires explicit user approval before execution in case the user has auto-approve mode enabled. Set to 'true' for potentially impactful operations like installing/uninstalling packages, deleting/overwriting files, system configuration changes, network operations, or any commands that could have unintended side effects. Set to 'false' for safe operations like reading files/directories, running development servers, building projects, and other non-destructive operations.
            <% if(enableTaskProgress) { %>- task_progress: (optional) A checklist showing task progress after this tool use is completed. (See 'Updating Task Progress' section for more details)<% } %>
            Usage:
            <execute_command>
            <command>Your command here</command>
            <requires_approval>true or false</requires_approval>
            <% if(enableTaskProgress) { %><task_progress>
            Checklist here (optional)
            </task_progress><% } %>
            </execute_command>
            
            <% for(tool in toolList){%>
            ## ${invoke(tool, "getName")}
            Description: ${invoke(tool, "description")}
            Parameters:
            ${invoke(tool, "parameters")}
            Usage:
            ${invoke(tool, "usage")}
            
            
            <% } %>
            
            # 我这里有一些内部mcp工具,如果发现内部mcp工具就可以用来解决问题,请优先使用内部mcp工具
            ## serverName:${internalServer.name}  ${internalServer.args}
            ### Available Tools
            ${mcpToolList}
            
            # Tool Use Guidelines
            
            1. In <thinking> tags, assess what information you already have and what information you need to proceed with the task.
            2. Choose the most appropriate tool based on the task and the tool descriptions provided. Assess if you need additional information to proceed, and which of the available tools would be most effective for gathering this information. For example using the list_files tool is more effective than running a command like \\`ls\\` in the terminal. It's critical that you think about each available tool and use the one that best fits the current step in the task.
            3. If multiple actions are needed, use one tool at a time per message to accomplish the task iteratively, with each tool use being informed by the result of the previous tool use. Do not assume the outcome of any tool use. Each step must be informed by the previous step's result.
            4. Formulate your tool use using the XML format specified for each tool.
            5. After each tool use, the user will respond with the result of that tool use. This result will provide you with the necessary information to continue your task or make further decisions. This response may include:
              - Information about whether the tool succeeded or failed, along with any reasons for failure.
              - Linter errors that may have arisen due to the changes you made, which you'll need to address.
              - New terminal output in reaction to the changes, which you may need to consider or act upon.
              - Any other relevant feedback or information related to the tool use.
            6. ALWAYS wait for user confirmation after each tool use before proceeding. Never assume the success of a tool use without explicit confirmation of the result from the user.
            7. When you can not decide which tool to use, you can use the chat tool to ask the user for help. If you are using the chat tool, you must return the message in Chinese中文.
            
            It is crucial to proceed step-by-step, waiting for the user's message after each tool use before moving forward with the task. This approach allows you to:
            1. Confirm the success of each step before proceeding.
            2. Address any issues or errors that arise immediately.
            3. Adapt your approach based on new information or unexpected results.
            4. Ensure that each action builds correctly on the previous ones.
            
            By waiting for and carefully considering the user's response after each tool use, you can react accordingly and make informed decisions about how to proceed with the task. This iterative process helps ensure the overall success and accuracy of your work.
            
            ====
            
            MCP SERVERS
            
            The Model Context Protocol (MCP) enables communication between the system and locally running MCP servers that provide additional tools and resources to extend your capabilities.
            
            # Connected MCP Servers
            
            When a server is connected, you can use the server's tools via the `use_mcp_tool` tool, and access the server's resources via the `access_mcp_resource` tool.
            
            <% for(server in serverList){ %>
            ## serverName:${server.name}  ${server.args}
            ### Available Tools
            ${server.tools}
            <% } %>
            
            
            ====
            
            RULES
            
            - NEVER end attempt_completion result with a question or request to engage in further conversation! Formulate the end of your result in a way that is final and does not require further input from the user.
            - You are STRICTLY FORBIDDEN from starting your messages with "Great", "Certainly", "Okay", "Sure". You should NOT be conversational in your responses, but rather direct and to the point. For example you should NOT say "Great, I've updated the CSS" but instead something like "I've updated the CSS". It is important you be clear and technical in your messages.
            - When presented with images, utilize your vision capabilities to thoroughly examine them and extract meaningful information. Incorporate these insights into your thought process as you accomplish the user's task.
            - At the end of each user message, you will automatically receive environment_details. This information is not written by the user themselves, but is auto-generated to provide potentially relevant context about the project structure and environment. While this information can be valuable for understanding the project context, do not treat it as a direct part of the user's request or response. Use it to inform your actions and decisions, but don't assume the user is explicitly asking about or referring to this information unless they clearly do so in their message. When using environment_details, explain your actions clearly to ensure the user understands, as they may not be aware of these details.
            - Before executing commands, check the "Actively Running Terminals" section in environment_details. If present, consider how these active processes might impact your task. For example, if a local development server is already running, you wouldn't need to start it again. If no active terminals are listed, proceed with command execution as normal.
            - MCP operations should be used one at a time, similar to other tool usage. Wait for confirmation of success before proceeding with additional operations.
            - When using the replace_in_file tool, you must include complete lines in your SEARCH blocks, not partial lines. The system requires exact line matches and cannot match partial lines. For example, if you want to match a line containing "const x = 5;", your SEARCH block must include the entire line, not just "x = 5" or other fragments.
            - When using the replace_in_file tool, if you use multiple SEARCH/REPLACE blocks, list them in the order they appear in the file. For example if you need to make changes to both line 10 and line 50, first include the SEARCH/REPLACE block for line 10, followed by the SEARCH/REPLACE block for line 50.
            ====
            
            SYSTEM INFORMATION
            
            Operating System: ${osName}
            Default Shell: ${defaultShell}
            Home Directory: ${homeDir}
            Current Working Directory: ${cwd}
            
            ====
            
            OBJECTIVE
            
            You accomplish a given task iteratively, breaking it down into clear steps and working through them methodically.
            
            1. Analyze the user's task and set clear, achievable goals to accomplish it. Prioritize these goals in a logical order.
            2. Work through these goals sequentially, utilizing available tools one at a time as necessary. Each goal should correspond to a distinct step in your problem-solving process. You will be informed on the work completed and what's remaining as you go.
            3. Remember, you have extensive capabilities with access to a wide range of tools that can be used in powerful and clever ways as necessary to accomplish each goal. Before calling a tool, do some analysis within <thinking></thinking> tags. First, analyze the file structure provided in environment_details to gain context and insights for proceeding effectively. Then, think about which of the provided tools is the most relevant tool to accomplish the user's task. Next, go through each of the required parameters of the relevant tool and determine if the user has directly provided or given enough information to infer a value. When deciding if the parameter can be inferred, carefully consider all the context to see if it supports a specific value. If all of the required parameters are present or can be reasonably inferred, close the thinking tag and proceed with the tool use. BUT, if one of the values for a required parameter is missing, DO NOT invoke the tool (not even with fillers for the missing params) and instead, ask the user to provide the missing parameters using the ask_followup_question tool. DO NOT ask for more information on optional parameters if it is not provided.
            4. Once you've completed the user's task, you must use the attempt_completion tool to present the result of the task to the user. You may also provide a CLI command to showcase the result of your task; this can be particularly useful for web development tasks, where you can run e.g. \\`open index.html\\` to show the website you've built.
            5. The user may provide feedback, which you can use to make improvements and try again. But DO NOT continue in pointless back and forth conversations, i.e. don't end your responses with questions or offers for further assistance.`
            
            ====
            
            USER'S CUSTOM INSTRUCTIONS
            
            The following additional instructions are provided by the user, and should be followed to the best of your ability without interfering with the TOOL USE guidelines.
            
            ${customInstructions}
            
            用户可能会定义一些使用工作的流程(Flow),会使用内部工具和Mcp工具,这个时候你需要严格按照用户的定义来执行这个工作流.如果用户没有定义则忽略掉这条规则.
            用户定义的工作流:
            ${workflow}
            
            ====
            
            
            
            <% if(enableTaskProgress) { %>
            ====
            
            UPDATING TASK PROGRESS
            
            Every tool use supports an optional task_progress parameter that allows you to provide an updated checklist to keep the user informed of your overall progress on the task. This should be used regularly throughout the task to keep the user informed of completed and remaining steps. Before using the attempt_completion tool, ensure the final checklist item is checked off to indicate task completion.
            
            - You probably wouldn't use this while in PLAN mode until the user has approved your plan and switched you to ACT mode.
            - Use standard Markdown checklist format: "- [ ]" for incomplete items and "- [x]" for completed items
            - Provide the whole checklist of steps you intend to complete in the task, and keep the checkboxes updated as you make progress. It's okay to rewrite this checklist as needed if it becomes invalid due to scope changes or new information.
            - Keep items focused on meaningful progress milestones rather than minor technical details. The checklist should not be so granular that minor implementation details clutter the progress tracking.
            - If you are creating this checklist for the first time, and the tool use completes the first step in the checklist, make sure to mark it as completed in your parameter input since this checklist will be displayed after this tool use is completed.
            - For simple tasks, short checklists with even a single item are acceptable. For complex tasks, avoid making the checklist too long or verbose.
            - If a checklist is being used, be sure to update it any time a step has been completed.
            
            Example:
            <use_mcp_tool>
            <server_name>weather-server</server_name>
            <tool_name>get_forecast</tool_name>
            <arguments>
            {
              "city": "San Francisco",
              "days": 5
            }
            </arguments>
            <task_progress>
            - [x] Set up project structure
            - [x] Install dependencies
            - [ ] Get weather data
            - [ ] Test application
            </task_progress>
            </use_mcp_tool>
            
            ====
            <% } %>
            
            """;

    public static final String ANALYSIS_PROMPT = """
             Based on the project information and requirements below, list all files that need to be modified or created.(你不需要返回任何修改意见) \s
             Current Project Analysis: \n
             %s \n
             Requirements: \n
             %s \n
            """;

    public static final String CODE_PROMPT = """
            \n
            \n
            + IMPORTANT:Use ONLY ONE of these two comments to indicate unchanged code:
                -  "// ... existing code ..."
                -  "// ... other methods ..."
            \n
            + IMPORTANT:如果你生成的代码中注释被你省略掉了,你需要在这个类中添加一行 // ... existing code ... 让我知道你省略了一些注释
            \n
            + IMPORTANT:In XML documents, use the following XML comment format to indicate unchanged code:
                   -  <!-- ... existing code ... -->
             \n
             如果你是修改文件,请必须遵循以下返回格式:
            
             Return edits similar to unified diffs that `diff -U0` would produce.
            
             Make sure you include the first 2 lines with the file paths.
             Don't include timestamps with the file paths.
            
             Start each chunk of changes with a `@@ ... @@` line.
             Don't include line numbers like `diff -U0` does.
             The user's patch tool doesn't need them.
            
             The user's patch tool needs CORRECT patches that apply cleanly against the current contents of the file!
             Think carefully and make sure you include and mark all lines that need to be removed or changed as `-` lines.
             Make sure you mark all new or modified lines with `+`.
             Don't leave out any lines or the diff patch won't apply correctly.
            
             Indentation matters in the diffs!
            
             Start a new hunk for each section of the file that needs changes.
            
             Only output hunks that specify changes with `+` or `-` lines.
             Skip any hunks that are entirely unchanging ` ` lines.
            
             Output hunks in whatever order makes the most sense.
             Hunks don't need to be in any particular order.
            
             When editing a function, method, loop, etc use a hunk to replace the *entire* code block.
             Delete the entire existing version with `-` lines and then add a new, updated version with `+` lines.
             This will help you generate correct code and correct diffs.
            
             To move code within a file, use 2 hunks: 1 to delete it from its current location, 1 to insert it in the new location.
            
             To make a new file, show a diff from `--- /dev/null` to `+++ path/to/new/file.ext`.
            
            
             例子:(只有修改文件用diff,添加文件不用diff)
             你一定要在修改的内容上部或者下部带回点内容,方便我diff的时候找到代码的位置.
             每一组修改使用:@@ ... @@  开头
                + 每一组的定义:
                    1.一个独立的方法
                    2.一个独立的字段
                    3.一个独立的内部类
            
             <boltAction type="file" filePath="src/main/java/com/example/service/UserService.java">
                ```diff
                --- src/main/java/com/example/service/PrimeService.java
                +++ src/main/java/com/example/service/PrimeService.java
            
                @@ ... @@
                                log.info("sum method called with parameters: a={}, b={}", a, b);
                                return a + b;
                            }
                 +
                 +    public BigInteger findPrimeDifference(int n1, int n2) {
                 +        BigInteger prime1 = findNthPrime(n1);
                 +        BigInteger prime2 = findNthPrime(n2);
                 +        return prime2.subtract(prime1);
                 +    }
            
                 }
                ```
             </boltAction>
            
             如果是创建新文件:(里边就不使用diff)
             <boltAction type="file" filePath="src/main/java/com/example/user/service/UserService.java">
                     package com.example.user.service;
                     @Service
                     public class UserService {
                       ...
                     }
            </boltAction>
             \n
             \n
            """;

}