package run.mone.hive.actions.python;

import com.google.common.collect.ImmutableMap;
import run.mone.hive.actions.Action;
import run.mone.hive.common.AiTemplate;
import run.mone.hive.schema.Message;
import run.mone.hive.utils.XmlParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;


public class ExecutePythonCode extends Action {


    private final String prompt = """
            You are a Python code executor. Your task is to generate a single line of Python code that executes the following function with appropriate parameters:
            
            ${code}
            
            Generate a line of code that calls the main function with suitable parameters and prints the result.
            The line should be in the format: print(function_name(param1, param2, ...))
            Wrap the code in <boltAction></boltAction> tags.
            
            Provide only the generated line of code without any additional explanations.
            
            example:
            <boltAction>
            print(sun(11,22))
            </boltAction>
            """;


    public ExecutePythonCode() {
        setFunction((req, action, context) -> {
            String code = context.getCtx().get("code").getAsString();
            String renderedPrompt = AiTemplate.renderTemplate(prompt, ImmutableMap.of(
                    "code", code
            ));
            String exeCmd = llm.chat(renderedPrompt);

            exeCmd = XmlParser.parser(exeCmd).get(0);

            String result = null;
            String newCode = code + "\n" + exeCmd;
            try {
                result = executePythonCode(newCode).getContent();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            context.getCtx().addProperty("code", newCode);
            context.getCtx().addProperty("error", result);
            return Message.builder().content(result).build();
        });
    }


    //写一段代码 调用 python ```$code``` 执行代码,并且捕获输出如果有错误也包含错误(class)
    public Message executePythonCode(String code) {
        try {
            ProcessBuilder processBuilder = new ProcessBuilder("python", "-c", code);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new RuntimeException("Python code execution failed with exit code " + exitCode);
            }
            return Message.builder().content(output.toString().trim()).build();
        } catch (Exception e) {
            return Message.builder().content(e.getMessage()).build();
        }
    }


}