
package run.mone.hive.actions.python;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import run.mone.hive.actions.Action;
import run.mone.hive.common.AiTemplate;
import run.mone.hive.schema.Message;
import run.mone.hive.utils.PythonExecutor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FixPythonBug extends Action {

    private final String bugFixPrompt = """
            You are a Python bug fixer. Your task is to fix the bug in the following Python code:
            
            ${code}
            
            The code produced the following error when executed:
            
            ${error}
            
            Please provide the corrected code without any additional explanations. Wrap the code in <code></code> tags.
            
            如果你发现返回的并不是错误,则直接返回源代码即可,同时也是放到<code></code>中
            """;

    private final PythonExecutor pythonExecutor;

    public FixPythonBug() {
        this.pythonExecutor = new PythonExecutor();
        setFunction((req, action, context) -> {
            String code = context.getCtx().get("code").toString();
            String error = context.getCtx().get("error").toString();
            String fixedCode = null;
            try {
                fixedCode = fixBug(code, error);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return Message.builder().content(fixedCode).build();
        });
    }

    private String fixBug(String buggyCode, String error) throws Exception {


        String renderedPrompt = AiTemplate.renderTemplate(bugFixPrompt, ImmutableMap.of(
                "code", buggyCode,
                "error", error
        ));

        String fixedCodeWithTags = llm.chat(renderedPrompt);
        return extractCodeFromTags(fixedCodeWithTags);
    }


    private String extractCodeFromTags(String codeWithTags) {
        Pattern pattern = Pattern.compile("<code>(.*?)</code>", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(codeWithTags);
        if (matcher.find()) {
            return matcher.group(1).trim();
        }
        return codeWithTags; // Return original string if no tags found
    }
}
