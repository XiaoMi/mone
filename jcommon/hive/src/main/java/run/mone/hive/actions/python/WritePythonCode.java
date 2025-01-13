package run.mone.hive.actions.python;

import com.google.common.collect.ImmutableMap;
import run.mone.hive.actions.WriteCode;
import run.mone.hive.common.AiTemplate;
import run.mone.hive.schema.Message;
import run.mone.hive.schema.MetaKey;
import run.mone.hive.schema.MetaValue;
import run.mone.hive.utils.XmlParser;

import java.util.List;

/**
 * @author goodjava@qq.com
 * @date 2025/1/5 13:59
 */
public class WritePythonCode extends WriteCode {

    private final String prompt = """
                        You are a Python code generator. Your task is to write a Python function named 'execute' that takes a single parameter 'params' of type dict. The function should implement the following requirements:
            
                        ${requirements}
            
                        Please provide only the function implementation without any additional explanations. 
                        Wrap the code in <boltAction></boltAction> tags.
            
                        Here's an example of a sum function:
            
                        <boltAction>
                        def execute(params):
                            a = params.get('a', 0)
                            b = params.get('b', 0)
                            return a + b
                        </boltAction>
            
                        Now, please implement the function based on the given requirements.
            """;

    public WritePythonCode() {
        setName("WritePythonCode");
        setDescription("");
        setFunction((req, action, context) -> {
            String message = req.getMessage().getContent();
            String str = AiTemplate.renderTemplate(prompt, ImmutableMap.of("requirements", message));
            String res = llm.syncChat(req.getRole(), str);
            List<String> list = XmlParser.parser(res);
            String code = list.get(0);
            context.getCtx().addProperty("code", code);
            return Message.builder().content(this.llm.chat(str)).meta(ImmutableMap.of(MetaKey.builder().key("code").build(), MetaValue.builder().value(code).build())).build();
        });
    }


}