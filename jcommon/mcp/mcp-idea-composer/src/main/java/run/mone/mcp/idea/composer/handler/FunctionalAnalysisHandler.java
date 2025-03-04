package run.mone.mcp.idea.composer.handler;

import org.apache.commons.lang3.StringUtils;
import run.mone.mcp.idea.composer.handler.biz.BotChainCallContext;
import run.mone.mcp.idea.composer.handler.biz.ComposerImagePo;
import run.mone.mcp.idea.composer.handler.biz.Const;
import run.mone.mcp.idea.composer.handler.prompt.Prompt;
import run.mone.mcp.idea.composer.handler.xml.StreamingXmlModifier;
import run.mone.mcp.idea.composer.handler.xml.XmlParserCallback;
import run.mone.mcp.idea.composer.service.ComposerService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * @author goodjava@qq.com
 * @date 2024/11/24 10:58
 */
public class FunctionalAnalysisHandler extends AbstractBotHandler {

    private final BotChainCallContext botChainCallContext;

    public static final String name = "stream_FunctionalAnalysisBot";

    public FunctionalAnalysisHandler(BotChainCallContext botChainCallContext) {
        super(name);
        this.botChainCallContext = botChainCallContext;
    }

    @Override
    public CompletableFuture<PromptResult> process(String prompt, PromptResult previousResult, ConversationContext context) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String response = getAnalysisResponse(prompt, previousResult, context);
                return new PromptResult(formatAnalysisResponse(response));
            } catch (Exception e) {
                return new PromptResult("Functional analysis failed: " + e.getMessage(), false);
            }
        });
    }

    public String getAnalysisResponse(String prompt, PromptResult previousResult, ConversationContext context) {
        //默认的botId:130462
        String enhancedPrompt = buildAnalysisPrompt(prompt, previousResult, context);
        addAiChatMessage(getDisplayPrompt(prompt, previousResult), enhancedPrompt, Role.user, context);
        String response = botChainCallContext.getBotClient().sendPrompt(enhancedPrompt, Prompt.FUNCTION_ANALYSIS_SYSTEM_PROMPT, buildComposerImagePo());
        return response;
    }

    //不过有用来发送过去的prompt,还有用来显示的prompt,帮我创建一个方法获取显示的prompt(class)
    public String getDisplayPrompt(String prompt, PromptResult previousResult) {
        return "Analyze which code needs to be modified";
    }

    private String buildAnalysisPrompt(String prompt, PromptResult previousResult, ConversationContext context) {
        //bug fix
        if (this.botChainCallContext.bugfix()) {
            return String.format("请分析以下错误信息和系统信息并提供给我造成这个bug的文件列表,以便我后边fix这个bug.  \n" +
                            "\n" +
                            "错误信息:  \n" +
                            "%s  \n" +
                            "\n" +
                            "当前系统状态:  \n" +
                            "%s  \n" +
                            "\n"
                    , botChainCallContext.getParams().get(Const.FIX_BUG_ERROR_INFO), previousResult.getContent());
        } else {
            //添加功能(列出文件列表)
            String msg = CodePrompt.ANALYSIS_PROMPT;
            return msg.formatted(previousResult.getContent(), prompt);
        }
    }

    private String formatAnalysisResponse(String response) {
        StringBuilder sb = new StringBuilder();
        Map<String, String> modifiedSubTypes = new HashMap<>();
            List<String> classList = new ArrayList<>();
            StreamingXmlModifier modifier = new StreamingXmlModifier(new XmlParserCallback() {

                @Override
                public void onArtifactStart(String id, String title) {

                }

                @Override
                public void onArtifactEnd() {

                }

                @Override
                public void onActionStart(String type, String subType, String filePath) {
                    if (StringUtils.isNotEmpty(subType) && (subType.equals("modify") || subType.equals("create"))) {
                        boolean fileExist = fileExist(filePath);
                        if (!fileExist && subType.equals("modify")) {
                            modifiedSubTypes.put(filePath, "create");
                        }
                        if (fileExist) {
                            String content = ComposerService.getFileByPath(filePath);
                            classList.add(content);
                        }
                    }
                }

                @Override
                public void onActionEnd() {

                }

                @Override
                public void onContentChar(char c) {

                }

            });

            String modifiedResponse = modifier.modifyXmlContent(response, modifiedSubTypes);
            sb.append(modifiedResponse).append("\n");
            if (!classList.isEmpty()) {
                sb.append("\n需要修改的类:\n").append(String.join("\n", classList));
            }

        return sb.toString();
    }

    private ComposerImagePo buildComposerImagePo() {
        Object image = botChainCallContext.getParams().get(Const.COMPOSER_IMAGE_CONTEXT);
        if (image != null) {
            return (ComposerImagePo) image;
        }
        return null;
    }

    private boolean fileExist(String filePath){
        return ComposerService.fileExist(filePath);
    }
}