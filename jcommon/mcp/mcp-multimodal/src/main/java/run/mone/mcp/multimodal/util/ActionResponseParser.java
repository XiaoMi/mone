package run.mone.mcp.multimodal.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Arrays;

/**
 * Utility class for parsing action outputs from the GUI agent model
 */
public class ActionResponseParser {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * Parse the output text from the model into a structured JSON format
     * 
     * @param outputText The raw text output from the model
     * @return JSON string with parsed action data
     */
    public static String parseActionOutput(String outputText) {
        // Create default result object
        ObjectNode result = objectMapper.createObjectNode();
        result.put("thought", "");
        result.put("action", "");
        result.putNull("key");
        result.putNull("content");
        result.putNull("start_box");
        result.putNull("end_box");
        result.putNull("direction");
        
        if (outputText == null || outputText.isEmpty()) {
            try {
                return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(result);
            } catch (Exception e) {
                return "{}";
            }
        }
        
        // Extract Thought part
        Pattern thoughtPattern = Pattern.compile("Thought:(.*?)\\nAction:", Pattern.DOTALL);
        Matcher thoughtMatcher = thoughtPattern.matcher(outputText);
        if (thoughtMatcher.find()) {
            String thought = thoughtMatcher.group(1).trim();
            result.put("thought", thought);
        }
        
        // Extract Action part
        Pattern actionPattern = Pattern.compile("Action:(.*?)(?:\\n|$)", Pattern.DOTALL);
        Matcher actionMatcher = actionPattern.matcher(outputText);
        if (actionMatcher.find()) {
            String actionText = actionMatcher.group(1).trim();
            
            if (!actionText.isEmpty()) {
                // Parse action type
                String[] actionParts = actionText.split("\\(", 2);
                String actionType = actionParts[0].trim();
                result.put("action", actionType);
                
                // Parse parameters if they exist
                if (actionParts.length > 1 && actionParts[1].endsWith(")")) {
                    String paramsText = actionParts[1].substring(0, actionParts[1].length() - 1);
                    
                    // Process each parameter
                    for (String param : paramsText.split(",")) {
                        param = param.trim();
                        if (param.contains("=")) {
                            String[] keyValue = param.split("=", 2);
                            String key = keyValue[0].trim();
                            String value = keyValue[1].trim().replaceAll("^['\"]|['\"]$", "");
                            
                            // Handle different parameter types
                            switch (key) {
                                case "start_box", "end_box":
                                    // Extract numbers from box format
                                    Pattern boxPattern = Pattern.compile("\\d+");
                                    Matcher boxMatcher = boxPattern.matcher(value);
                                    int[] coords = new int[4];
                                    int index = 0;
                                    
                                    while (boxMatcher.find() && index < 4) {
                                        coords[index++] = Integer.parseInt(boxMatcher.group());
                                    }
                                    
                                    if (index == 4) {
                                        result.putArray(key).add(coords[0]).add(coords[1]).add(coords[2]).add(coords[3]);
                                    }
                                    break;
                                    
                                case "key":
                                    result.put(key, value);
                                    break;
                                    
                                case "content":
                                    // Handle escape characters
                                    value = value.replace("\\n", "\n")
                                            .replace("\\\"", "\"")
                                            .replace("\\'", "'");
                                    result.put(key, value);
                                    break;
                                    
                                case "direction":
                                    result.put(key, value);
                                    break;
                            }
                        }
                    }
                }
            }
        }
        
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(result);
        } catch (Exception e) {
            return "{}";
        }
    }
} 