
package run.mone.hive.llm;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import run.mone.hive.configs.LLMConfig;
import run.mone.hive.roles.Teacher;
import run.mone.hive.schema.AiMessage;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
class LLMTest {

    private LLM llm;

    private LLMConfig config;

    private String prompt = """
            你是一个浏览器操作专家.你总是能把用户的需求,翻译成专业的操作指令.
            你支持的指令:
            1.打开tab,并且带上url,用来打开某个页面
            <action type="openTab">
            </action>
            
            1.创建新标签页
            问题:
            新建标签页,打开baidu
            
            返回结果:
            <action type="createNewTab" url="https://www.baidu.com">
            打开百度
            </action>
            
            2.关闭某个tab,带上tabName
            <action type="closeTab">
            $message
            </action>
            
            3.截屏,截取屏幕(当前可视区域)
            <action type="screenshot">
            $message
            </action>
            
            4.渲染页面
            <action type="buildDomTree">
            $message
            </action>
            
            5.取消渲染
            <action type="cancelBuildDomTree">
            $message
            </action>
            
            
            6.取消标记页面元素
            
            7.滚动一屏屏幕
            <action type="scrollOneScreen">
            $message
            </action>
            
            8.当给你一张截图,并且让你返回合适的action列表的时候,你就需要返回这个action类型了(这个action往往是多个 name=click(点击)  fill=填入内容  enter=回车  elementId=要操作的元素id,截图和源码里都有)
            //尽量一次返回一个页面的所有action操作
            //选哪个和element最近的数字
            //数字的颜色和这个元素的框是一个颜色
            <action type="action" name="fill" elementId="12" value="冰箱">
            在搜索框里输入冰箱
            </action>
            
            <action type="action" name="click" elementId="13">
            点击搜索按钮
            </action>
            
            
            9.产生通知
            <action type="notification">
            $message
            </action>
            
            10.获取当前窗口的所有标签
            <action type="getCurrentWindowTabs" service="tabManager">
            </action>
            
            返回的内容格式:
            
            <action type="$type">
            </action>
            
            Example:
            打开tab:
            <action type="tab" url="http://www.baidu.com">
            </action>
            
            截屏:
            <action type="screenshot">
            </action>
            
            用户需求:
            %s
            
            你的返回:
            
            """;


    @BeforeEach
    void setUp() {
        config = new LLMConfig();
        config.setDebug(false);
        config.setJson(false);
//        config.setLlmProvider(LLMProvider.DOUBAO);
//        config.setLlmProvider(LLMProvider.GOOGLE);
        config.setLlmProvider(LLMProvider.GOOGLE_2);
//        config.setLlmProvider(LLMProvider.OPENROUTER);//这个默认使用的是:anthropic/claude-3.5-sonnet:beta
//        config.setModel("google/gemini-2.0-flash-exp:free");
//        config.setModel("anthropic/claude-3.5-haiku-20241022");
//        config.setModel("qwen/qwen-max");

//        config.setLlmProvider(LLMProvider.DEEPSEEK);
//        config.setLlmProvider(LLMProvider.QWEN);
//        config.setLlmProvider(LLMProvider.MOONSHOT);
//        config.setModel("moonshot-v1-128k-vision-preview");

        //google通过cloudflare代理
        if (config.getLlmProvider() == LLMProvider.GOOGLE_2) {
            config.setUrl(System.getenv("GOOGLE_AI_GATEWAY") + "generateContent");
        }

        //openrouter 也需要使用代理
        if (config.getLlmProvider() == LLMProvider.OPENROUTER) {
            config.setUrl(System.getenv("OPENROUTER_AI_GATEWAY"));
        }

        llm = new LLM(config);


        // FIXME： 注意注意注意!!! 当使用Openrouter时，需要配置代理
        // 设置HTTP代理
        // System.setProperty("http.proxyHost", "127.0.0.1");
        // System.setProperty("http.proxyPort", "7890");
        // // 设置HTTPS代理
        // System.setProperty("https.proxyHost", "127.0.0.1");
        // System.setProperty("https.proxyPort", "7890");
        // // 设置SOCKS代理
        // System.setProperty("socksProxyHost", "127.0.0.1");
        // System.setProperty("socksProxyPort", "7890");
        // // 设置不需要代理的主机
        // System.setProperty("http.nonProxyHosts", "localhost|127.0.0.1");
    }

    @Test
    void testAskInDebugMode() throws ExecutionException, InterruptedException {
        String prompt = "Hello, world!";
        CompletableFuture<String> future = llm.ask(prompt);
        String result = future.get();
        System.out.println(result);
    }

    @Test
    void testChat() {
        String prompt = "hi";
        String result = llm.chat(prompt);
        log.info("{}", result);
        assertNotNull(result);
    }

    @Test
    void testChatWithImage() {
        List<AiMessage> messages = new ArrayList<>();
        JsonObject obj = new JsonObject();

        String text = "输入搜索信息,然后点击搜索按钮";

        text = """
                根据不同的页面返回不同的action列表:
                页面:需要执行的动作
                jd首页:搜索冰箱
                搜素详情页:点击排名第一的连接
                商品详情页:点击加入购物车按钮
                购物车加购页面:点击去购物车结算按钮
                分析出action列表(你每次只需要返回这个页面的action列表)
                你先要分析出来现在是那个页面(首页,搜索详情页,商品详情页,购物车加购页面)
                然后根据页面返回对应的action列表
                thx
                """;

        JsonObject req = new JsonObject();

        if (llm.getConfig().getLlmProvider() == LLMProvider.GOOGLE_2) {
            JsonArray parts = new JsonArray();
            obj.addProperty("text", text);
            JsonObject obj2 = new JsonObject();
            JsonObject objImg = new JsonObject();
            objImg.addProperty("mime_type", "image/jpeg");
            objImg.addProperty("data", llm.imageToBase64("/tmp/abc.jpeg", "jpeg"));
            obj2.add("inline_data", objImg);
            parts.add(obj);
            parts.add(obj2);
            req.add("parts", parts);
        }

        if (llm.getConfig().getLlmProvider() == LLMProvider.OPENROUTER || llm.getConfig().getLlmProvider() == LLMProvider.MOONSHOT) {
            req.addProperty("role", "user");
            JsonArray array = new JsonArray();

            JsonObject obj1 = new JsonObject();
            obj1.addProperty("type","text");
            obj1.addProperty("text",text);
            array.add(obj1);

            JsonObject obj2 = new JsonObject();
            obj2.addProperty("type","image_url");
            JsonObject img = new JsonObject();
            img.addProperty("url","data:image/jpeg;base64,"+llm.imageToBase64("/tmp/abc.jpeg", "jpeg"));
            obj2.add("image_url",img);
            array.add(obj2);

            req.add("content", array);
        }


        messages.add(AiMessage.builder().jsonContent(req).build());
        String sysPrompt = "你是一个聪明的人类,我给你一张浏览器的页面 和我提供的需求,你帮我分析下这个页面干什么的? thx";
        sysPrompt = prompt.formatted("返回合理的action列表");
        String result = llm.chatCompletion(messages, sysPrompt);
        log.info("{}", result);
        assertNotNull(result);
    }

    @Test
    public void testJson() {
        String res = llm.chat(Lists.newArrayList(AiMessage.builder().role("user").content("1+1=?").build()), LLMConfig.builder().json(true).build());
        System.out.println(res);
    }

    @Test
    public void testChat2() {
        String res = llm.chat(Lists.newArrayList(AiMessage.builder().role("user").content("a=12").build(), AiMessage.builder().role("user").content("2*a+a=?").build()));
        System.out.println(res);
    }

    @Test
    void testGetApiUrl() {
        String apiUrl = llm.getApiUrl("");
        assertEquals("https://api.stepfun.com/v1/chat/completions", apiUrl);
    }

    @Test
    void testGetApiUrlGoogle() {
        llm.setGoogle(true);
        String apiUrl = llm.getApiUrl("");
        assertEquals("https://generativelanguage.googleapis.com/v1beta/openai/chat/completions", apiUrl);
    }

    @Test
    public void testWebSearch() {
        String apiKey = System.getenv(config.getLlmProvider().getEnvName());
        String res = llm.chatCompletion(apiKey, Lists.newArrayList(AiMessage.builder().role("user").content("苏轼最好的10首词").build()), config.getLlmProvider().getDefaultModel(), "", LLMConfig.builder().webSearch(true).build());
        System.out.println(res);
    }

    @Test
    void testChatCompletionStream() throws InterruptedException {
        String apiKey = System.getenv(config.getLlmProvider().getEnvName());
        List<AiMessage> messages = new ArrayList<>();

        String c = """
                =======Rules you must follow
                You are Cline, a highly skilled software engineer with extensive knowledge in many programming languages, frameworks, design patterns, and best practices.
                
                ====
                
                TOOL USE
                
                You have access to a set of tools that are executed upon the user's approval. You can use one tool per message, and will receive the result of that tool use in the user's response. You use tools step-by-step to accomplish a given task, with each tool use informed by the result of the previous tool use.
                
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
                </use_mcp_tool>
                
                # Tool Use Examples
                ## Example 1: Requesting to use an MCP tool
                
                <use_mcp_tool>
                <server_name>weather-server</server_name>
                <tool_name>get_forecast</tool_name>
                <arguments>
                {
                  "city": "San Francisco",
                  "days": 5
                }
                </arguments>
                </use_mcp_tool>
                
                ## ask_followup_question
                Description: Ask the user a question to gather additional information needed to complete the task. This tool should be used when you encounter ambiguities, need clarification, or require more details to proceed effectively. It allows for interactive problem-solving by enabling direct communication with the user. Use this tool judiciously to maintain a balance between gathering necessary information and avoiding excessive back-and-forth.
                Parameters:
                - question: (required) The question to ask the user. This should be a clear, specific question that addresses the information you need.
                Usage:
                <ask_followup_question>
                <question>Your question here</question>
                </ask_followup_question>
                
                
                ## attempt_completion
                Description: After each tool use, the user will respond with the result of that tool use, i.e. if it succeeded or failed, along with any reasons for failure. Once you've received the results of tool uses and can confirm that the task is complete, use this tool to present the result of your work to the user. Optionally you may provide a CLI command to showcase the result of your work. The user may respond with feedback if they are not satisfied with the result, which you can use to make improvements and try again.
                IMPORTANT NOTE: This tool CANNOT be used until you've confirmed from the user that any previous tool uses were successful. Failure to do so will result in code corruption and system failure. Before using this tool, you must ask yourself in <thinking></thinking> tags if you've confirmed from the user that any previous tool uses were successful. If not, then DO NOT use this tool.
                Parameters:
                - result: (required) The result of the task. Formulate this result in a way that is final and does not require further input from the user. Don't end your result with questions or offers for further assistance.
                - command: (optional) A CLI command to execute to show a live demo of the result to the user. For example, use \\`open index.html\\` to display a created html website, or \\`open localhost:3000\\` to display a locally running development server. But DO NOT use commands like \\`echo\\` or \\`cat\\` that merely print text. This command should be valid for the current operating system. Ensure the command is properly formatted and does not contain any harmful instructions.
                Usage:
                <attempt_completion>
                <result>
                Your final result description here
                </result>
                <command>Command to demonstrate result (optional)</command>
                </attempt_completion>
                
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
                
                ## serverName:filesystem-mcp \s
                ### Available Tools
                name:filesystem_executor
                description:Execute filesystem operations
                inputSchema:{"type":"object","properties":{"operation":{"type":"string","enum":["read_file","read_multiple_files","write_file","edit_file","create_directory","list_directory","directory_tree","move_file","search_files","get_file_info","list_allowed_directories"],"description":"Type of filesystem operation to execute"},"path":{"type":"string","description":"File or directory path"},"paths":{"type":"array","items":{"type":"string"},"description":"Array of file paths for read_multiple_files operation"},"content":{"type":"string","description":"Content to write to file"},"edits":{"type":"array","items":{"type":"object","properties":{"oldText":{"type":"string"},"newText":{"type":"string"}},"required":["oldText","newText"]},"description":"Array of edit operations for edit_file operation"},"dryRun":{"type":"boolean","description":"Preview changes without applying them"},"source":{"type":"string","description":"Source path for move_file operation"},"destination":{"type":"string","description":"Destination path for move_file operation"},"pattern":{"type":"string","description":"Search pattern for search_files operation"},"excludePatterns":{"type":"array","items":{"type":"string"},"description":"Patterns to exclude in search_files operation"}},"required":["operation"]}
                ## serverName:playwright-mcp \s
                ### Available Tools
                name:playwright_navigate
                description:Navigate to a URL
                inputSchema:{"type":"object","properties":{"url":{"type":"string"},"width":{"type":"number","description":"Viewport width in pixels (default: 1920)"},"height":{"type":"number","description":"Viewport height in pixels (default: 1080)"},"timeout":{"type":"number","description":"Navigation timeout in milliseconds"},"waitUntil":{"type":"string","description":"Navigation wait condition"},"headless":{"type":"boolean","description":"Whether to run in headless mode (default: false)"}},"required":["url"]}
                
                name:playwright_click
                description:Click an element on the page
                inputSchema:{"type":"object","properties":{"selector":{"type":"string","description":"CSS selector for element to click"}},"required":["selector"]}
                
                name:playwright_screenshot
                description:Take a screenshot of the current page or a specific element
                inputSchema:{"type":"object","properties":{"name":{"type":"string","description":"Name for the screenshot"},"selector":{"type":"string","description":"CSS selector for element to screenshot"},"width":{"type":"number","description":"Width in pixels (default: 800)"},"height":{"type":"number","description":"Height in pixels (default: 600)"},"storeBase64":{"type":"boolean","description":"Store screenshot in base64 format (default: true)"},"savePng":{"type":"boolean","description":"Save screenshot as PNG file (default: false)"},"downloadsDir":{"type":"string","description":"Custom downloads directory path (default: user\\u0027s Downloads folder)"}},"required":["name"]}
                
                name:playwright_fill
                description:Fill out an input field
                inputSchema:{"type":"object","properties":{"selector":{"type":"string","description":"CSS selector for input field"},"value":{"type":"string","description":"Value to fill"}},"required":["selector","value"]}
                
                name:playwright_select
                description:Select an element on the page with Select tag
                inputSchema:{"type":"object","properties":{"selector":{"type":"string","description":"CSS selector for element to select"},"value":{"type":"string","description":"Value to select"}},"required":["selector","value"]}
                
                name:playwright_hover
                description:Hover an element on the page
                inputSchema:{"type":"object","properties":{"selector":{"type":"string","description":"CSS selector for element to hover"}},"required":["selector"]}
                
                name:playwright_evaluate
                description:Execute JavaScript in the browser console
                inputSchema:{"type":"object","properties":{"script":{"type":"string","description":"JavaScript code to execute"}},"required":["script"]}
                
                name:playwright_get_content
                description:Get content from the current page or a specific element
                inputSchema:{"type":"object","properties":{"selector":{"type":"string","description":"CSS selector for target element (optional)"},"contentType":{"type":"string","enum":["text","html"],"description":"Type of content to retrieve (default: text)"},"wait":{"type":"boolean","description":"Whether to wait for element to be present (default: true)"},"timeout":{"type":"number","description":"Maximum time to wait in milliseconds (default: 30000)"},"waitForLoadState":{"type":"string","enum":["load","domcontentloaded","networkidle"],"description":"Wait for specific load state (default: load)"},"waitForSelector":{"type":"string","description":"Additional selector to wait for before getting content (optional)"}}}
                
                name:playwright_get
                description:Perform an HTTP GET request
                inputSchema:{"type":"object","properties":{"url":{"type":"string","description":"URL to perform GET operation"}},"required":["url"]}
                
                name:playwright_post
                description:Perform an HTTP POST request
                inputSchema:{"type":"object","properties":{"url":{"type":"string","description":"URL to perform POST operation"},"value":{"type":"string","description":"Data to post in the body"}},"required":["url","value"]}
                
                name:playwright_put
                description:Perform an HTTP PUT request
                inputSchema:{"type":"object","properties":{"url":{"type":"string","description":"URL to perform PUT operation"},"value":{"type":"string","description":"Data to PUT in the body"}},"required":["url","value"]}
                
                name:playwright_delete
                description:Perform an HTTP DELETE request
                inputSchema:{"type":"object","properties":{"url":{"type":"string","description":"URL to perform DELETE operation"}},"required":["url"]}
                
                name:playwright_patch
                description:Perform an HTTP PATCH request
                inputSchema:{"type":"object","properties":{"url":{"type":"string","description":"URL to perform PATCH operation"},"value":{"type":"string","description":"Data to PATCH in the body"}},"required":["url","value"]}
                
                name:playwright_cleanup
                description:Cleanup all Playwright resources (browser, page, contexts)
                inputSchema:{"type":"object","properties":{"force":{"type":"boolean","description":"Force cleanup even if operations are in progress (default: false)"}}}
                
                name:playwright_new_tab
                description:Open a new browser tab
                inputSchema:{"type":"object","properties":{"url":{"type":"string","description":"URL to open in new tab"},"name":{"type":"string","description":"Name for the new tab"},"active":{"type":"boolean","description":"Whether to switch to the new tab (default: true)"},"width":{"type":"number","description":"Viewport width in pixels (default: 1920)"},"height":{"type":"number","description":"Viewport height in pixels (default: 1080)"}},"required":["url","name"]}
                
                name:playwright_switch_tab
                description:Switch to a specific browser tab by name
                inputSchema:{"type":"object","properties":{"name":{"type":"string","description":"Name of the tab to switch to"},"waitForLoadState":{"type":"string","enum":["load","domcontentloaded","networkidle"],"description":"Wait for specific load state after switching (default: load)"},"timeout":{"type":"number","description":"Maximum time to wait in milliseconds (default: 30000)"}},"required":["name"]}
                ## serverName:applescript-mcp \s
                ### Available Tools
                name:appleScriptOperation
                description:Execute AppleScript commands on macOS
                inputSchema:{"type":"object","properties":{"command":{"type":"string","enum":["restart","shutdown","sleep","logout","mute","unmute","volume","custom"],"description":"The AppleScript command to execute"},"arguments":{"type":"array","items":{"type":"string"},"description":"Additional arguments for the command (e.g., volume level)"},"customCommand":{"type":"string","description":"Custom AppleScript command to execute when command is set to \\u0027custom\\u0027"}},"required":["command"]}
                ## serverName:database-mcp \s
                ### Available Tools
                name:mysql_executor
                description:Execute MySQL operations (query, update, DDL)
                inputSchema:{"type":"object","properties":{"type":{"type":"string","enum":["query","update","ddl"],"description":"Type of SQL operation to execute"},"sql":{"type":"string","description":"SQL statement to execute"}},"required":["type","sql"]}
                
                name:sqlite_executor
                description:Execute SQLite operations (query, update, DDL)
                inputSchema:{"type":"object","properties":{"type":{"type":"string","enum":["query","update","ddl"],"description":"Type of SQL operation to execute"},"sql":{"type":"string","description":"SQL statement to execute"}},"required":["type","sql"]}
                ## serverName:memory-mcp \s
                ### Available Tools
                name:create_entities
                description:Create multiple new entities in the knowledge graph
                inputSchema:{"type":"object","properties":{"entities":{"type":"array","items":{"type":"object","properties":{"name":{"type":"string","description":"The name of the entity"},"entityType":{"type":"string","description":"The type of the entity"},"observations":{"type":"array","items":{"type":"string"},"description":"An array of observation contents associated with the entity"}},"required":["name","entityType","observations"]}}},"required":["entities"]}
                
                name:create_relations
                description:Create multiple new relations between entities in the knowledge graph. Relations should be in active voice
                inputSchema:{"type":"object","properties":{"relations":{"type":"array","items":{"type":"object","properties":{"from":{"type":"string","description":"The name of the entity where the relation starts"},"to":{"type":"string","description":"The name of the entity where the relation ends"},"relationType":{"type":"string","description":"The type of the relation"}},"required":["from","to","relationType"]}}},"required":["relations"]}
                
                name:add_observations
                description:Add new observations to existing entities in the knowledge graph
                inputSchema:{"type":"object","properties":{"observations":{"type":"array","items":{"type":"object","properties":{"entityName":{"type":"string","description":"The name of the entity to add the observations to"},"contents":{"type":"array","items":{"type":"string"},"description":"An array of observation contents to add"}},"required":["entityName","contents"]}}},"required":["observations"]}
                
                name:delete_entities
                description:Delete multiple entities and their associated relations from the knowledge graph
                inputSchema:{"type":"object","properties":{"entityNames":{"type":"array","items":{"type":"string"},"description":"An array of entity names to delete"}},"required":["entityNames"]}
                
                name:delete_relations
                description:Delete multiple relations from the knowledge graph
                inputSchema:{"type":"object","properties":{"relations":{"type":"array","items":{"type":"object","properties":{"from":{"type":"string","description":"The name of the entity where the relation starts"},"to":{"type":"string","description":"The name of the entity where the relation ends"},"relationType":{"type":"string","description":"The type of the relation"}},"required":["from","to","relationType"]}}},"required":["relations"]}
                
                name:delete_observations
                description:Delete specific observations from entities in the knowledge graph
                inputSchema:{"type":"object","properties":{"deletions":{"type":"array","items":{"type":"object","properties":{"entityName":{"type":"string","description":"The name of the entity containing the observations"},"observations":{"type":"array","items":{"type":"string"},"description":"An array of observations to delete"}},"required":["entityName","observations"]}}},"required":["deletions"]}
                
                name:read_graph
                description:Read the entire knowledge graph
                inputSchema:{"type":"object","properties":{}}
                
                name:search_nodes
                description:Search for nodes in the knowledge graph based on a query
                inputSchema:{"type":"object","properties":{"query":{"type":"string","description":"The search query to match against entity names, types, and observation content"}},"required":["query"]}
                
                name:open_nodes
                description:Open specific nodes in the knowledge graph by their names
                inputSchema:{"type":"object","properties":{"names":{"type":"array","items":{"type":"string"},"description":"An array of entity names to retrieve"}},"required":["names"]}
                
                
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
                
                Operating System: Mac OS X
                Default Shell: bash
                Home Directory: /Users/zhangzhiyong
                Current Working Directory: cwd
                
                ====
                
                BJECTIVE
                
                You accomplish a given task iteratively, breaking it down into clear steps and working through them methodically.
                
                1. Analyze the user's task and set clear, achievable goals to accomplish it. Prioritize these goals in a logical order.
                2. Work through these goals sequentially, utilizing available tools one at a time as necessary. Each goal should correspond to a distinct step in your problem-solving process. You will be informed on the work completed and what's remaining as you go.
                3. Remember, you have extensive capabilities with access to a wide range of tools that can be used in powerful and clever ways as necessary to accomplish each goal. Before calling a tool, do some analysis within <thinking></thinking> tags. First, analyze the file structure provided in environment_details to gain context and insights for proceeding effectively. Then, think about which of the provided tools is the most relevant tool to accomplish the user's task. Next, go through each of the required parameters of the relevant tool and determine if the user has directly provided or given enough information to infer a value. When deciding if the parameter can be inferred, carefully consider all the context to see if it supports a specific value. If all of the required parameters are present or can be reasonably inferred, close the thinking tag and proceed with the tool use. BUT, if one of the values for a required parameter is missing, DO NOT invoke the tool (not even with fillers for the missing params) and instead, ask the user to provide the missing parameters using the ask_followup_question tool. DO NOT ask for more information on optional parameters if it is not provided.
                4. Once you've completed the user's task, you must use the attempt_completion tool to present the result of the task to the user. You may also provide a CLI command to showcase the result of your task; this can be particularly useful for web development tasks, where you can run e.g. \\`open index.html\\` to show the website you've built.
                5. The user may provide feedback, which you can use to make improvements and try again. But DO NOT continue in pointless back and forth conversations, i.e. don't end your responses with questions or offers for further assistance.`
                
                ====
                
                USER'S CUSTOM INSTRUCTIONS
                
                The following additional instructions are provided by the user, and should be followed to the best of your ability without interfering with the TOOL USE guidelines.
                
                
                
                
                =======Chat history
                user:
                hi
                
                =======Latest Questions
                user:
                hi
                """;

//        c = "Hello, can you tell me a short joke?";


        messages.add(AiMessage.builder().role("user").content(c).build());
        String model = config.getLlmProvider().getDefaultModel();

//        model = "deepseek-reasoner";

        StringBuilder responseBuilder = new StringBuilder();
        List<JsonObject> jsonResponses = new ArrayList<>();
        CountDownLatch latch = new CountDownLatch(1);

        llm.chatCompletionStream(
                apiKey,
                messages,
                model,
                (content, jsonResponse) -> {
                    if ("[DONE]".equals(content)) {
                        latch.countDown();
                    } else {
                        System.out.println(content);
                        responseBuilder.append(content);
                        jsonResponses.add(jsonResponse);
                    }
                },
                line -> log.info("Received line: {}", line)
        );

        latch.await();

        String fullResponse = responseBuilder.toString();
        log.info("Full response: {}", fullResponse);

        assertFalse(fullResponse.isEmpty(), "Response should not be empty");
        assertFalse(jsonResponses.isEmpty(), "Should have received JSON responses");
    }

    @Test
    public void testChatWithBot() {
        // 初始化LLM并配置Bot桥接
        llm.setBotBridge(new BotHttpBridge(
                "xxxxxxxxxx",
                "xxxxxxxxx",
                "xxxxxx",
                "xxxxxxx"
        ));

        Teacher aaa = new Teacher("aaa");

        // 简单调用
        String simple = llm.chatWithBot(aaa, "你好");
        System.out.println("simple call : " + simple);

        // 带参数调用
        JsonObject params = new JsonObject();
        params.addProperty("key", "value");
        String withParam = llm.chatWithBot(aaa, "你好", params);
        System.out.println("with param : " + withParam);

        // 自定义响应处理
        String response = llm.chatWithBot(aaa, "你好", params, res -> {
            // 自定义处理逻辑
            System.out.println("function call : " + res);
            return res;
        });
    }
}

