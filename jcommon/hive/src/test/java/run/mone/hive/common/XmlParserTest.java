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
}
