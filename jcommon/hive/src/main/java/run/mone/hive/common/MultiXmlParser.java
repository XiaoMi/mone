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

    public static final String MULTI_MESSAGE_SPLIT_CHAR = ":·:";

    public List<ToolDataInfo> parse(String input) {
        if (StringUtils.isEmpty(input)) {
            return Lists.newArrayList();
        }
        
        List<ToolDataInfo> list = new ArrayList<>();
        
        // Find all top-level tags
        Matcher outerMatcher = Pattern.compile("<(\\w+)>(.*?)</\\1>", Pattern.DOTALL).matcher(input);
        
        while (outerMatcher.find()) {
            String outerTag = outerMatcher.group(1);
            String outerContent = outerMatcher.group(2);
            
            // Parse each tag's content
            Map<String, String> keyValuePairs = new LinkedHashMap<>();
            Matcher innerMatcher = Pattern.compile("<(\\w+)>(.*?)</\\1>", Pattern.DOTALL).matcher(outerContent);
            
            while (innerMatcher.find()) {
                String key = innerMatcher.group(1);
                String value = innerMatcher.group(2);
                
                String exists = keyValuePairs.get(key);
                if (null != exists) {
                    exists = exists + MULTI_MESSAGE_SPLIT_CHAR + value.trim();
                    keyValuePairs.put(key, exists);
                } else {
                    keyValuePairs.put(key, value.trim());
                }
            }
            
            list.add(new ToolDataInfo(outerTag, keyValuePairs));
        }
        
        return list;
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