package run.mone.mcp.linuxagent.llm;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import  org.apache.http.client.methods.HttpGet;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

public class Gemini {

    private final String geminiApiUrl;
    private final String geminiApiKey;
    private final String linuxServerUrl;
    private final int gridSize = 100;

    public Gemini(String geminiApiBaseUrl, String geminiApiKey, String linuxServerUrl) {
        this.geminiApiUrl = geminiApiBaseUrl+"/v1beta/models/gemini-1.5-pro-latest:generateContent";
        this.geminiApiKey = geminiApiKey;
        this.linuxServerUrl = linuxServerUrl;
    }

    private Map<String, String> captureFullscreenJpgBase64() throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet request = new HttpGet(linuxServerUrl + "/capture_screen?grid=true");
        CloseableHttpResponse response = httpClient.execute(request);

        try {
            HttpEntity entity = response.getEntity();
            if (response.getStatusLine().getStatusCode() != 200) {
                throw new IOException("Failed to capture screen: " + EntityUtils.toString(entity));
            }
            JsonObject jsonResponse = new Gson().fromJson(EntityUtils.toString(entity), JsonObject.class);
            Map<String, String> result = new HashMap<>();
            result.put("data", jsonResponse.get("data").getAsString());
            result.put("mime_type", jsonResponse.get("mime_type").getAsString()); // Hardcoded for now
            return result;
        } finally {
            response.close();
            httpClient.close();
        }
    }
    private Map<String, String> captureGridJpgBase64(int gridNumber) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet request = new HttpGet(linuxServerUrl + "/capture_grid?grid=" + gridNumber);
        CloseableHttpResponse response = httpClient.execute(request);

        try {
            HttpEntity entity = response.getEntity();
            if (response.getStatusLine().getStatusCode() != 200) {
                throw new IOException("Failed to capture grid: " + EntityUtils.toString(entity));
            }

            JsonObject jsonResponse = new Gson().fromJson(EntityUtils.toString(entity), JsonObject.class);
            Map<String, String> result = new HashMap<>();
            result.put("data", jsonResponse.get("data").getAsString());
            result.put("mime_type", jsonResponse.get("mime_type").getAsString()); // Hardcoded for now
            return result;

        } finally {
            response.close();
            httpClient.close();
        }
    }

    private  Map<String, Object> getPoint(int gridNumber, int width, int height, int select) {
        int x = (gridNumber / (int)(Math.ceil((double)height / gridSize))) * gridSize;
        int y = (gridNumber % (int)(Math.ceil((double)height / gridSize))) * gridSize;

        int offx = x;
        int offy = y;

        // 定义九个点的坐标和编号
        int[][] points = {
                {offx + 0, offy + 0},  // 左上角
                {offx + gridSize / 2, offy + 0},  // 上边中点
                {offx + gridSize - 1, offy + 0},  // 右上角
                {offx + 0, offy + gridSize / 2},  // 左边中点
                {offx + gridSize / 2, offy + gridSize / 2},  // 中心
                {offx + gridSize - 1, offy + gridSize / 2},  // 右边中点
                {offx + 0, offy + gridSize - 1},  // 左下角
                {offx + gridSize / 2, offy + gridSize - 1},  // 下边中点
                {offx + gridSize - 1, offy + gridSize - 1},  // 右下角
        };

        Map<String, Object> result = new HashMap<>();
        result.put("x", points[select][0]);
        result.put("y", points[select][1]);
        return result;
    }

    public Map<String, Object> getObjectLocation(String target) throws IOException {
        Map<String, String> base64Data = captureFullscreenJpgBase64();
        String base64Str = base64Data.get("data");
        String mimeType = base64Data.get("mime_type");
        //构建请求体
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("model", "gemini-2.0-flash-exp");

        JsonArray contents = new JsonArray();
        contents.add(new Gson().toJsonTree(String.format("请找出包含 '%s' 的网格编号：", target)));
        contents.add(new Gson().toJsonTree("当前截图："));
        JsonObject imagePart = new JsonObject();
        imagePart.addProperty("inlineData", base64Str);
        contents.add(imagePart);
        requestBody.add("contents", contents);

        JsonObject config = new JsonObject();

        JsonObject toolConfig = new JsonObject();
        JsonObject functionCallingConfig = new JsonObject();
        functionCallingConfig.addProperty("mode", "ANY");
        toolConfig.add("function_calling_config", functionCallingConfig);

        JsonArray tools = new JsonArray();
        JsonObject tool = new JsonObject();
        JsonArray functionDeclarations = new JsonArray();
        JsonObject functionDeclaration = new JsonObject();
        functionDeclaration.addProperty("name", "set_grid_number");
        functionDeclaration.addProperty("description", "包含目标的网格");

        JsonObject parameters = new JsonObject();
        JsonObject properties = new JsonObject();
        JsonObject gridNumberParam = new JsonObject();
        gridNumberParam.addProperty("type", "NUMBER");
        gridNumberParam.addProperty("description", "网格编号");
        properties.add("grid_number", gridNumberParam);
        parameters.add("properties", properties);
        parameters.addProperty("type", "OBJECT");

        JsonArray requiredParams = new JsonArray();
        requiredParams.add("grid_number");
        parameters.add("required", requiredParams);

        functionDeclaration.add("parameters", parameters);
        functionDeclarations.add(functionDeclaration);
        tool.add("function_declarations", functionDeclarations);
        tools.add(tool);
        toolConfig.add("tools", tools);
        config.add("tool_config", toolConfig);
        requestBody.add("config", config);
        requestBody.addProperty("system_instruction", "找出包含目标的网格编号，然后调用set_grid_number函数指定网格。");


        //发起请求
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(geminiApiUrl + "?key=" + geminiApiKey);
        httpPost.setHeader("Content-Type", "application/json");
        StringEntity stringEntity = new StringEntity(requestBody.toString(), ContentType.APPLICATION_JSON);
        httpPost.setEntity(stringEntity);

        CloseableHttpResponse response = httpClient.execute(httpPost);

        try {
            HttpEntity entity = response.getEntity();
            if (response.getStatusLine().getStatusCode() != 200) {
                Map<String, Object> errorResult = new HashMap<>();
                errorResult.put("x", null);
                errorResult.put("y", null);
                errorResult.put("error", "Gemini API error: " + EntityUtils.toString(entity));
                return errorResult;
            }
            String responseBody = EntityUtils.toString(entity);
            System.out.println("Response: " + responseBody);

            JsonObject jsonResponse = new Gson().fromJson(responseBody, JsonObject.class);

            JsonArray candidates = jsonResponse.getAsJsonArray("candidates");
            if (candidates.size() == 0) {
                Map<String, Object> errorResult = new HashMap<>();
                errorResult.put("x", null);
                errorResult.put("y", null);
                errorResult.put("error", "No candidates found");
                return errorResult;
            }
            JsonObject firstCandidate = candidates.get(0).getAsJsonObject();
            JsonObject content = firstCandidate.getAsJsonObject("content");
            JsonArray parts = content.getAsJsonArray("parts");

            for (int i = 0; i < parts.size(); i++) {
                JsonObject part = parts.get(i).getAsJsonObject();
                if (part.has("functionCall")) {
                    JsonObject functionCall = part.getAsJsonObject("functionCall");
                    String functionName = functionCall.get("name").getAsString();
                    if ("set_grid_number".equals(functionName)) {
                        int gridNumber = functionCall.getAsJsonObject("args").get("grid_number").getAsInt();

                        Map<String, String> gridImgData = captureGridJpgBase64(gridNumber);
                        String gridImgBase64 = gridImgData.get("data");
                        String gridImgMimeType = gridImgData.get("mime_type");

                        //构建请求体
                        JsonObject requestBody2 = new JsonObject();
                        requestBody2.addProperty("model", "gemini-2.0-flash-exp");

                        JsonArray contents2 = new JsonArray();
                        contents2.add(new Gson().toJsonTree(String.format("请定位出可以点击到 '%s' 的点：",target)));
                        contents2.add(new Gson().toJsonTree("局部截图："));
                        JsonObject imagePart2 = new JsonObject();
                        imagePart2.addProperty("inlineData", gridImgBase64);
                        contents2.add(imagePart2);
                        requestBody2.add("contents", contents2);

                        JsonObject config2 = new JsonObject();
                        JsonObject toolConfig2 = new JsonObject();
                        JsonObject functionCallingConfig2 = new JsonObject();
                        functionCallingConfig2.addProperty("mode", "ANY");
                        toolConfig2.add("function_calling_config", functionCallingConfig2);

                        JsonArray tools2 = new JsonArray();
                        JsonObject tool2 = new JsonObject();

                        JsonArray functionDeclarations2 = new JsonArray();
                        JsonObject functionDeclaration2 = new JsonObject();
                        functionDeclaration2.addProperty("name", "set_point_number");
                        functionDeclaration2.addProperty("description", "以点击到目标的点");
                        JsonObject parameters2 = new JsonObject();
                        JsonObject properties2 = new JsonObject();
                        JsonObject pointNumberParam = new JsonObject();
                        pointNumberParam.addProperty("type", "NUMBER");
                        pointNumberParam.addProperty("description", "点编号");
                        properties2.add("point_number", pointNumberParam);
                        parameters2.add("properties", properties2);
                        parameters2.addProperty("type", "OBJECT");
                        JsonArray required2 = new JsonArray();
                        required2.add("point_number");
                        parameters2.add("required", required2);
                        functionDeclaration2.add("parameters", parameters2);
                        functionDeclarations2.add(functionDeclaration2);
                        tool2.add("function_declarations", functionDeclarations2);
                        tools2.add(tool2);
                        toolConfig2.add("tools", tools2);
                        config2.add("tool_config", toolConfig2);
                        requestBody2.add("config", config2);
                        requestBody2.addProperty("system_instruction", "找出请找出可以点击到目标的点的编号，然后调用set_point_number函数指定该点。");

                        //发起请求
                        CloseableHttpClient httpClient2 = HttpClients.createDefault();
                        HttpPost httpPost2 = new HttpPost(geminiApiUrl + "?key=" + geminiApiKey);
                        httpPost2.setHeader("Content-Type", "application/json");
                        StringEntity stringEntity2 = new StringEntity(requestBody2.toString(), ContentType.APPLICATION_JSON);
                        httpPost2.setEntity(stringEntity2);
                        CloseableHttpResponse response2 = httpClient2.execute(httpPost2);

                        try {
                            HttpEntity entity2 = response2.getEntity();
                            if (response2.getStatusLine().getStatusCode() != 200) {
                                Map<String, Object> errorResult = new HashMap<>();
                                errorResult.put("x", null);
                                errorResult.put("y", null);
                                errorResult.put("error", "Gemini API error: " + EntityUtils.toString(entity2));
                                return errorResult;
                            }
                            String responseBody2 = EntityUtils.toString(entity2);
                            System.out.println("Response2: " + responseBody2);
                            JsonObject jsonResponse2 = new Gson().fromJson(responseBody2, JsonObject.class);
                            JsonArray candidates2 = jsonResponse2.getAsJsonArray("candidates");

                            if(candidates2.size() == 0){
                                Map<String, Object> errorResult = new HashMap<>();
                                errorResult.put("x", null);
                                errorResult.put("y", null);
                                errorResult.put("error", "No candidates found in the second response");
                                return errorResult;
                            }

                            JsonObject firstCandidate2 = candidates2.get(0).getAsJsonObject();
                            JsonObject content2 = firstCandidate2.getAsJsonObject("content");
                            JsonArray parts2 = content2.getAsJsonArray("parts");

                            for (int j = 0; j < parts2.size(); j++) {
                                JsonObject part2 = parts2.get(j).getAsJsonObject();
                                if (part2.has("functionCall")) {
                                    JsonObject functionCall2 = part2.getAsJsonObject("functionCall");
                                    String functionName2 = functionCall2.get("name").getAsString();
                                    if ("set_point_number".equals(functionName2)) {
                                        int pointNumber = functionCall2.getAsJsonObject("args").get("point_number").getAsInt();
                                        // Assuming you have width and height from somewhere, like the initial image
                                        // For demonstration, let's assume you have them
                                        // You might need to get them from the initial image response or pass them as parameters
                                        int width = 1920; // Replace with actual width
                                        int height = 1080; // Replace with actual height
                                        return getPoint(gridNumber, width, height, pointNumber);
                                    }
                                }
                            }
                        } finally {
                            response2.close();
                            httpClient2.close();
                        }
                    }
                }
            }
        } finally {
            response.close();
            httpClient.close();
        }
        Map<String, Object> errorResult = new HashMap<>();
        errorResult.put("x", null);
        errorResult.put("y", null);
        errorResult.put("error", "Not Found");
        return errorResult;
    }
}
