package run.mone.moner.server.common;

import org.junit.Test;

public class MultiXmlParserTest {

    @Test
    public void testJsonToXml() {
        // Given
        MultiXmlParser parser = new MultiXmlParser();
        String jsonInput = """
                {
                    "action1": {
                        "type": "action",
                        "name": "fill",
                        "elementId": "12",
                        "value": "冰箱"
                    },
                    "action2": {
                        "type": "action",
                        "name": "click",
                        "elementId": "13",
                        "desc": "点击搜索按钮"
                    }
                }""";

        // When
        String xmlOutput = parser.jsonToXml(jsonInput);

        // Then
        String expectedXml = """
                
                <action type="action" name="fill" elementId="12" value="冰箱">
                </action>
                
                <action type="action" name="click" elementId="13">
                            点击搜索按钮
                </action>
                """.replaceAll("(?m)^[ ]+", "            "); // 统一缩进

        // 移除所有空白字符后比较
        String normalizedExpected = expectedXml.replaceAll("\\s+", "");
        String normalizedActual = xmlOutput.replaceAll("\\s+", "");

    }
}