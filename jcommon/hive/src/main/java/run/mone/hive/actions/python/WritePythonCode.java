package run.mone.hive.actions.python;

import com.google.common.collect.ImmutableMap;
import run.mone.hive.actions.WriteCode;
import run.mone.hive.common.AiTemplate;

/**
 * @author goodjava@qq.com
 * @date 2025/1/5 13:59
 */
public class WritePythonCode extends WriteCode {

    private final String prompt = """
                        You are a Python code generator. Your task is to write a Python function named 'execute' that takes a single parameter 'params' of type dict. The function should implement the following requirements:
            
                        ${requirements}
            
                        Please provide only the function implementation without any additional explanations. Wrap the code in <code></code> tags.
            
                        Here's an example of a sum function:
            
                        <code>
                        def execute(params):
                            a = params.get('a', 0)
                            b = params.get('b', 0)
                            return a + b
                        </code>
            
                        Now, please implement the function based on the given requirements.
            """;

    public WritePythonCode() {
        setName("WritePythonCode");
        setDescription("");
        setFunction((req, action) -> {
            String message = req.getMessage().getContent();
            String str = AiTemplate.renderTemplate(prompt, ImmutableMap.of("requirements", message));
            return this.llm.chat(str);
        });
    }
}