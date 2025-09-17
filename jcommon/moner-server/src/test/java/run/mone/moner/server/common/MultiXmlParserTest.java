package run.mone.moner.server.common;

import org.junit.Test;

import java.util.List;

public class MultiXmlParserTest {


    @Test
    public void test2() {
        String xml = """
                <thinking>
                The user wants to search for "李清照". This falls under the "Searcher_Chrome" role, which is for web searching. I need to follow the steps outlined in that role:
                1. Open a Google tab (if no code is present, which seems to be the case here since it says "发现没有code的时候,必须调用这个接口").
                2. Enter the search query in the Google search bar and click the search button.
                3. Find a Wikipedia page and click the link.
                4. Collect information from the Wikipedia page and end.
                
                Therefore, the first step is to open a Google tab.
                </thinking>
                ```xml
                <use_mcp_tool>
                <server_name>chrome-server</server_name>
                <tool_name>OpenTabAction</tool_name>
                <arguments>
                {
                  "url": "https://www.google.com/"
                }
                </arguments>
                </use_mcp_tool>
                ```
                """;

        MultiXmlParser parser = new MultiXmlParser();
        List<Result> list = parser.parse(xml);
        System.out.println(list);
    }


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

    @Test
    public void testJsonToXmlWithNewOperations() {
        // Given
        MultiXmlParser parser = new MultiXmlParser();
        String jsonInput = """
                {
                    "action1": {
                        "type": "action",
                        "name": "focus",
                        "elementId": "14",
                        "desc": "聚焦到输入框"
                    },
                    "action2": {
                        "type": "action",
                        "name": "search",
                        "elementId": "15",
                        "value": "搜索关键词",
                        "desc": "执行搜索操作"
                    },
                    "action3": {
                        "type": "action",
                        "name": "select",
                        "elementId": "16",
                        "value": "选项值",
                        "desc": "选择下拉框选项"
                    }
                }""";

        // When
        String xmlOutput = parser.jsonToXml(jsonInput);

        // Then
        String expectedXml = """

                <action type="action" name="focus" elementId="14">
                            聚焦到输入框
                </action>

                <action type="action" name="search" elementId="15" value="搜索关键词">
                            执行搜索操作
                </action>

                <action type="action" name="select" elementId="16" value="选项值">
                            选择下拉框选项
                </action>
                """.replaceAll("(?m)^[ ]+", "            "); // 统一缩进

        // 移除所有空白字符后比较
        String normalizedExpected = expectedXml.replaceAll("\\s+", "");
        String normalizedActual = xmlOutput.replaceAll("\\s+", "");

        // Basic validation that the new operations are included
        assert xmlOutput.contains("name=\"focus\"");
        assert xmlOutput.contains("name=\"search\"");
        assert xmlOutput.contains("name=\"select\"");
    }
}