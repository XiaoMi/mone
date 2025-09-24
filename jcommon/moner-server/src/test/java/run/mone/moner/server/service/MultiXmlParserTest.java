package run.mone.moner.server.service;

import org.junit.Test;
import run.mone.moner.server.common.MultiXmlParser;
import run.mone.moner.server.common.Result;

import java.util.List;

/**
 * @author goodjava@qq.com
 * @date 2025/2/7 18:25
 */
public class MultiXmlParserTest {

    @Test
    public void test1() {
        String x = """
                 Usage:
            <attempt_completion>
            <result>
            Your final result description here
            </result>
            <command>Command to demonstrate result (optional)</command>
            </attempt_completion>
            
            # Tool Use Guidelines
            
            <use_mcp_tool>
            <server_name>weather-server</server_name>
            <tool_name>get_forecast</tool_name>
            <arguments>
            {
              "city": "San Francisco",
              "days": 5
            }
            </arguments>
            </use_mcp_tool>
            
            <use_mcp_tool>
                <server_name>chrome-server</server_name>
                <tool_name>OperationAction</tool_name>
                <arguments>
                {
                "action1": {
                    "type": "action",
                    "name": "fill",
                    "elementId": "12",
                    "value": "冰箱",
                    "desc": "在搜索框输入"
                  },
                  "action2": {
                    "type": "action",
                    "name": "click",
                    "elementId": "13",
                    "value": "",
                    "desc": "点击搜索按钮"
                  }
                }
                </arguments>
                </use_mcp_tool>
            
                """;



        List<Result> l = new MultiXmlParser().parse(x);
        System.out.println(l);
    }
}
