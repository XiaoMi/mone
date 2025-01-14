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

    //帮我写一个函数去除一个字符串开头和结尾的空格和\n\s之类色,thx(class)
    public static String trimString(String str) {
        if (str == null) {
            return null;
        }
        return str.trim().replaceAll("^[\\s\\n]+|[\\s\\n]+$", "");
    }

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
