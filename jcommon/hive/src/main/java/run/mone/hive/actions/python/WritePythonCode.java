package run.mone.hive.actions.python;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonParser;
import run.mone.hive.actions.WriteCode;
import run.mone.hive.common.AiTemplate;
import run.mone.hive.common.JsonUtils;
import run.mone.hive.common.StreamingXmlParser;
import run.mone.hive.common.XmlParserCallbackAdapter;
import run.mone.hive.schema.Message;
import run.mone.hive.schema.MetaKey;
import run.mone.hive.schema.MetaValue;

import java.util.ArrayList;
import java.util.List;

/**
 * @author goodjava@qq.com
 * @date 2025/1/5 13:59
 */
public class WritePythonCode extends WriteCode {

    private final String prompt = """
                        You are a Python code generator. Your task is to write a Python function named 'execute' that takes a single parameter 'params' of type dict. The function should implement the following requirements:
            
                        ${requirements}
            
                        Please provide only the function implementation without any additional explanations. Wrap the code in <boltAction></boltAction> tags.
            
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
            List<String> codeList = new ArrayList<>();
            StringBuilder sb = new StringBuilder();
            new StreamingXmlParser(new XmlParserCallbackAdapter() {
                @Override
                public void onActionStart(String type, String subType, String filePath) {
                    sb.setLength(0);
                }

                @Override
                public void onActionEnd() {
                    codeList.add(sb.toString());
                    sb.setLength(0);
                }

                @Override
                public void onContentChar(char c) {
                    sb.append(c);
                }
            }).append(str);
            context.getCtx().add("code", JsonUtils.gson.toJsonTree(codeList));
            return Message.builder().content(this.llm.chat(str)).meta(ImmutableMap.of(MetaKey.builder().key("code").build(), MetaValue.builder().value(codeList).build())).build();
        });
    }
}