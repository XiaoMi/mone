package run.mone.hive.common;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MultiXmlParser {
    private static final Pattern XML_PATTERN = Pattern.compile("<(\\w+)>(.*?)(?:</\\1>|$)", Pattern.DOTALL);
    private static final Pattern KEY_VALUE_PATTERN = Pattern.compile("<(\\w+)>(.*?)(?:</\\1>|$)", Pattern.DOTALL);
    public static final String MULTI_MESSAGE_SPLIT_CHAR = ":·:";

    public List<Result> parse(String input) {
        if (StringUtils.isEmpty(input)) {
            return Lists.newArrayList();
        }
        String outerTag = null;
        Matcher xmlMatcher = XML_PATTERN.matcher(input);

        List<Result> list = new ArrayList<>();

        while (xmlMatcher.find()) {
            outerTag = xmlMatcher.group(1);
            String xmlContent = xmlMatcher.group(2);
            Map<String, String> keyValuePairs = extractKeyValuePairs(xmlContent);
            list.add(new Result(outerTag, keyValuePairs));
        }
        return list;
    }

    private Map<String, String> extractKeyValuePairs(String xmlContent) {
        Map<String, String> keyValuePairs = new LinkedHashMap<>();
        Matcher keyValueMatcher = KEY_VALUE_PATTERN.matcher(xmlContent);

        while (keyValueMatcher.find()) {
            String key = keyValueMatcher.group(1);
            String value = keyValueMatcher.group(2);

            // 递归处理嵌套的XML结构
            if (value.contains("<") && value.contains(">")) {
                Map<String, String> nestedPairs = extractKeyValuePairs(value);
                value = nestedPairs.toString();
            }

            String exists = keyValuePairs.get(key);
            if (null != exists) {
                exists = exists + MULTI_MESSAGE_SPLIT_CHAR + value.trim();
                keyValuePairs.put(key, exists);
            } else {
                keyValuePairs.put(key, value.trim());
            }
        }

        return keyValuePairs;
    }

    @SneakyThrows
    public String jsonToXml(String jsonStr) {
        try {
            JsonObject json = JsonParser.parseString(jsonStr).getAsJsonObject();
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document doc = docBuilder.newDocument();

            StringBuilder xmlBuilder = new StringBuilder();

            json.entrySet().forEach(entry -> {
                JsonObject actionObj = entry.getValue().getAsJsonObject();
                xmlBuilder.append("\n            <action");
                
                // Add all properties as attributes
                actionObj.entrySet().forEach(prop -> {
                    if (!prop.getValue().isJsonObject()) {
                        xmlBuilder.append(" ")
                                .append(prop.getKey())
                                .append("=\"")
                                .append(prop.getValue().getAsString())
                                .append("\"");
                    }
                });
                
                xmlBuilder.append(">\n");
                
                // If desc exists, add it as content
                if (actionObj.has("desc")) {
                    xmlBuilder.append("            ")
                            .append(actionObj.get("desc").getAsString())
                            .append("\n");
                }
                
                xmlBuilder.append("            </action>\n");
            });

            return xmlBuilder.toString();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException("Failed to create XML document", e);
        }
    }
}