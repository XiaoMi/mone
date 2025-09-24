package run.mone.hive.common;

import org.junit.jupiter.api.Test;

/**
 * @author goodjava@qq.com
 * @date 2025/1/6 17:05
 */
public class StreamTest {

    @Test
    public void test1() {
        String code = """
                            You are a Python code generator. Your task is to write a Python function named 'execute' that takes a single parameter 'params' of type dict. The function should implement the following requirements:
                
                            计算一个excel有多少个工作区
                
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

        StringBuilder sb = new StringBuilder();
        StreamingXmlParser parser = new StreamingXmlParser(new XmlParserCallbackAdapter(){

            @Override
            public void onActionStart(String type, String subType, String filePath) {
                sb.setLength(0);
            }

            @Override
            public void onActionEnd() {
                System.out.println(sb.toString());
            }

            @Override
            public void onContentChar(char c) {
                sb.append(c);
            }
        });
        parser.append(code);

    }

}
