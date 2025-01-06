
package run.mone.hive.actions.python;

import com.google.common.collect.ImmutableMap;
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
            """;

    private final PythonExecutor pythonExecutor;

    public FixPythonBug() {
        this.pythonExecutor = new PythonExecutor();
        setFunction((req, action) -> {
            String buggyCode = req.getMessage().getContent();
            String fixedCode = null;
            try {
                fixedCode = fixBug(buggyCode);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return Message.builder().content("<code>" + fixedCode + "</code>").build();
        });
    }

    private String fixBug(String buggyCode) throws Exception {
        String params = generateParameters(buggyCode);
        String executionResult = pythonExecutor.executePythonCode(buggyCode, params);
        
        if (!executionResult.toLowerCase().contains("error")) {
            return buggyCode; // No error, return original code
        }

        String renderedPrompt = AiTemplate.renderTemplate(bugFixPrompt, ImmutableMap.of(
                "code", buggyCode,
                "error", executionResult
        ));

        String fixedCodeWithTags = llm.chat(renderedPrompt);
        return extractCodeFromTags(fixedCodeWithTags);
    }

    private String generateParameters(String code) {
        // Reuse the parameter generation logic from ExecutePythonCode
        String paramGenerationPrompt = """
                You are an AI assistant specialized in analyzing Python code and generating appropriate test parameters. Your task is to examine the following Python code and suggest suitable parameters for testing:
                
                ${code}
                
                Please provide a set of test parameters that will effectively exercise the main functionality of this code. Consider the following guidelines:
                
                1. Identify the main function or entry point of the code.
                2. Determine the expected input types (e.g., integers, strings, lists, dictionaries).
                3. Include edge cases and typical use cases in your parameter suggestions.
                4. If the code requires specific formats (e.g., date strings, file paths), provide examples that match those formats.
                5. If the code interacts with external resources (e.g., files, APIs), suggest mock data or placeholders.
                
                Format your response as a Python dictionary or a list of arguments, depending on how the main function accepts input. For example:
                
                {"param1": value1, "param2": value2} or [arg1, arg2, arg3]
                
                Provide only the parameter suggestions without any additional explanation.
                """;

        String promptForParams = AiTemplate.renderTemplate(paramGenerationPrompt, ImmutableMap.of("code", code));
        return llm.chat(promptForParams);
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
