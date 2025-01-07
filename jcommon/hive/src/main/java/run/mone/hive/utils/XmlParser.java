package run.mone.hive.utils;

import run.mone.hive.common.StreamingXmlParser;
import run.mone.hive.common.XmlParserCallbackAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author goodjava@qq.com
 * @date 2025/1/7 14:14
 */
public class XmlParser {

    public static List<String> parser(String str) {
        List<String> list = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        new StreamingXmlParser(new XmlParserCallbackAdapter() {
            @Override
            public void onActionStart(String type, String subType, String filePath) {
                sb.setLength(0);
            }

            @Override
            public void onActionEnd() {
                list.add(sb.toString());
                sb.setLength(0);
            }

            @Override
            public void onContentChar(char c) {
                sb.append(c);
            }
        }).append(str);
        return list;
    }

}
