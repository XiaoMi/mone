
package run.mone.mcp.idea.composer.handler.xml;

import java.util.Map;
import java.util.regex.Pattern;

public class StreamingXmlModifier extends StreamingXmlParser {

    public StreamingXmlModifier(XmlParserCallback callback) {
        super(callback);
    }

    public String modifyXmlContent(String originalXml, Map<String, String> modifiedSubTypes) {
        this.append(originalXml);
        return applyModifications(originalXml, modifiedSubTypes);
    }

    private String applyModifications(String xml, Map<String, String> modifiedSubTypes) {
        for (Map.Entry<String, String> entry : modifiedSubTypes.entrySet()) {
            String filePath = entry.getKey();
            String newSubType = entry.getValue();
            xml = xml.replaceAll(
                    "(<boltAction(?:[^>]*?\\s)?)subType=\"modify\"((?:[^>]*?\\s)?filePath=\"" + Pattern.quote(filePath) + "\"[^>]*>)",
                    "$1subType=\"" + newSubType + "\"$2"
            );

        }
        return xml;
    }
}
