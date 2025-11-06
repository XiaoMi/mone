package run.mone.mcp.multimodal.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for parsing action outputs from the GUI agent model
 * Supports the new format with <point>x y</point> syntax
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
        result.putNull("point");
        result.putNull("start_point");
        result.putNull("end_point");
        result.putNull("key");
        result.putNull("content");
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
        Pattern actionPattern = Pattern.compile("Action:\\s*(.*?)(?:#|\\n|$)", Pattern.DOTALL);
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
                    
                    // Parse parameters using regex to handle complex values
                    parseParameters(paramsText, result);
                }
            }
        }
        
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(result);
        } catch (Exception e) {
            return "{}";
        }
    }
    
    /**
     * Parse parameters from the action text
     */
    private static void parseParameters(String paramsText, ObjectNode result) {
        // Pattern to match key=value pairs, handling quotes and <point> tags
        Pattern paramPattern = Pattern.compile("(\\w+)\\s*=\\s*(['\"].*?['\"]|<point>.*?</point>|[^,]+)");
        Matcher paramMatcher = paramPattern.matcher(paramsText);
        
        while (paramMatcher.find()) {
            String key = paramMatcher.group(1).trim();
            String value = paramMatcher.group(2).trim();
            
            // Remove surrounding quotes if present
            value = value.replaceAll("^['\"]|['\"]$", "");
            
            // Handle different parameter types
            switch (key) {
                case "point":
                case "start_point":
                case "end_point":
                    // Parse <point>x y</point> format
                    int[] coords = parsePoint(value);
                    if (coords != null && coords.length == 2) {
                        result.putArray(key).add(coords[0]).add(coords[1]);
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
    
    /**
     * Parse point from <point>x y</point> format
     * 
     * @param pointText The point text (may include <point> tags)
     * @return Array with [x, y] coordinates, or null if parsing fails
     */
    private static int[] parsePoint(String pointText) {
        if (pointText == null || pointText.isEmpty()) {
            return null;
        }
        
        // Extract content from <point>x y</point> tags
        Pattern pointPattern = Pattern.compile("<point>\\s*(\\d+)\\s+(\\d+)\\s*</point>");
        Matcher pointMatcher = pointPattern.matcher(pointText);
        
        if (pointMatcher.find()) {
            try {
                int x = Integer.parseInt(pointMatcher.group(1));
                int y = Integer.parseInt(pointMatcher.group(2));
                return new int[]{x, y};
            } catch (NumberFormatException e) {
                return null;
            }
        }
        
        return null;
    }
} 