package run.mone.hive.common;

import org.junit.Test;
import run.mone.hive.utils.XmlParser;

import java.util.List;

/**
 * @author goodjava@qq.com
 * @date 2025/2/5 20:43
 */
public class XmlParserTest {

    @Test
    public void test1() {
        List<XmlParser.ActionItem> list = XmlParser.parser0("""
                ```xml
                <boltAction type="shopping" subType="小汽车" url="https://www.jd.com">
                用户想购买小汽车
                </boltAction>
                ```
                """);
        System.out.println(list);
    }


    @Test
    public void test2() {
        String data = """
                <file>
                <operation>write</operation>
                <path>/Users/zhangzhiyong/quick_sort.py</path>
                <content>
                def quick_sort(arr):
                    if len(arr) <= 1:
                        return arr
                    else:
                        pivot = arr[0]
                        less = [x for x in arr[1:] if x <= pivot]
                        greater = [x for x in arr[1:] if x > pivot]
                        return quick_sort(less) + [pivot] + quick_sort(greater)
                
                # 测试代码
                if __name__ == "__main__":
                    test_array = [64, 34, 25, 12, 22, 11, 90]
                    print("原始数组:", test_array)
                    sorted_array = quick_sort(test_array)
                    print("排序后数组:", sorted_array)
                </content>
                </file>
                """;

        List<ToolDataInfo> tools = new MultiXmlParser().parse(data);
        System.out.println(tools);
    }


    @Test
    public void test3() {
        String data = """
                 <use_mcp_tool>
            <server_name>weather-server</server_name>
            <tool_name>get_forecast</tool_name>
            <arguments>
            {
              "city": "San Francisco",
              "days": 5
            }
            </arguments>
            <task_progress>
            - [x] Set up project structure
            - [x] Install dependencies
            - [ ] Get weather data
            - [ ] Test application
            </task_progress>
            </use_mcp_tool>
            """;
        List<ToolDataInfo> tools = new MultiXmlParser().parse(data);
        System.out.println(tools);
    }

}
