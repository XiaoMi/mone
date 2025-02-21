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
import org.apache.http.client.methods.HttpGet;

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
        this.geminiApiUrl = geminiApiBaseUrl;
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
            result.put("mime_type", jsonResponse.get("mime_type").getAsString());
            result.put("width", jsonResponse.get("width").getAsString());
            result.put("height", jsonResponse.get("height").getAsString());
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

    private Map<String, Object> getPoint(int gridNumber, int width, int height, int select) {
        int x = (gridNumber / (int) (Math.ceil((double) height / gridSize))) * gridSize;
        int y = (gridNumber % (int) (Math.ceil((double) height / gridSize))) * gridSize;

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

    private JsonObject callGeminiApi(String requestBody) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpPost httpPost = new HttpPost(geminiApiUrl + "/v1beta/models/gemini-2.0-flash:generateContent" + "?key=" + geminiApiKey);
        httpPost.setHeader("Content-Type", "application/json");
        StringEntity stringEntity = new StringEntity(requestBody, ContentType.APPLICATION_JSON);
        httpPost.setEntity(stringEntity);

        CloseableHttpResponse response = httpClient.execute(httpPost);

        try {
            HttpEntity entity = response.getEntity();
            if (response.getStatusLine().getStatusCode() != 200) {
                throw new IOException("Gemini API error: " + EntityUtils.toString(entity));
            }
            String responseBody = EntityUtils.toString(entity);
            System.out.println("Response: " + responseBody);
            return new Gson().fromJson(responseBody, JsonObject.class);
        } finally {
            response.close();
            httpClient.close();
        }
    }

    public Map<String, Object> getObjectLocation(String target) throws IOException {
        Map<String, String> base64Data = captureFullscreenJpgBase64();
        String base64Str = base64Data.get("data");
        String mimeType = base64Data.get("mime_type");
        int width = Integer.parseInt(base64Data.get("width")); // Replace with actual width
        int height = Integer.parseInt(base64Data.get("height")); // Replace with actual height
        //构建请求体
        String requestBody = template.formatted("找出包含目标的网格编号，然后调用set_grid_number函数指定网格。",
                "set_grid_number", "包含目标的网格", "grid_number", "网格编号", "grid_number",
                "请找出包含 '%s' 的网格编号：".formatted(target), "当前截图", mimeType, base64Str);

        JsonObject jsonResponse = callGeminiApi(requestBody);

        JsonArray candidates = jsonResponse.getAsJsonArray("candidates");
        if (candidates.size() == 0) {
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("x", null);
            errorResult.put("y", null);
            errorResult.put("error", "No candidates found");
            return errorResult;
        }
        JsonObject firstCandidate = candidates.get(0).getAsJsonObject();
        JsonObject content1 = firstCandidate.getAsJsonObject("content");
        JsonArray parts1 = content1.getAsJsonArray("parts");

        for (int i = 0; i < parts1.size(); i++) {
            JsonObject part = parts1.get(i).getAsJsonObject();
            if (part.has("functionCall")) {
                JsonObject functionCall = part.getAsJsonObject("functionCall");
                String functionName = functionCall.get("name").getAsString();
                if ("set_grid_number".equals(functionName)) {
                    int gridNumber = functionCall.getAsJsonObject("args").get("grid_number").getAsInt();

                    Map<String, String> gridImgData = captureGridJpgBase64(gridNumber);
                    String gridImgBase64 = gridImgData.get("data");
                    String gridImgMimeType = gridImgData.get("mime_type");

                    String requestBody2 = template.formatted("找出请找出可以点击到目标的点的编号，然后调用set_point_number函数指定该点。",
                            "set_point_number", "可以点击到目标的点", "point_number", "点编号", "point_number",
                            "请定位出可以点击到 '%s' 的点：".formatted(target), "局部截图", gridImgMimeType, gridImgBase64);

                    JsonObject jsonResponse2 = callGeminiApi(requestBody2);

                    JsonArray candidates2 = jsonResponse2.getAsJsonArray("candidates");

                    if (candidates2.size() == 0) {
                        Map<String, Object> errorResult = new HashMap<>();
                        errorResult.put("x", null);
                        errorResult.put("y", null);
                        errorResult.put("error", "No candidates found in the second response");
                        return errorResult;
                    }

                    JsonObject firstCandidate2 = candidates2.get(0).getAsJsonObject();
                    JsonObject content3 = firstCandidate2.getAsJsonObject("content");
                    JsonArray parts3 = content3.getAsJsonArray("parts");

                    for (int j = 0; j < parts3.size(); j++) {
                        JsonObject part2 = parts3.get(j).getAsJsonObject();
                        if (part2.has("functionCall")) {
                            JsonObject functionCall2 = part2.getAsJsonObject("functionCall");
                            String functionName2 = functionCall2.get("name").getAsString();
                            if ("set_point_number".equals(functionName2)) {
                                int pointNumber = functionCall2.getAsJsonObject("args").get("point_number").getAsInt();
                                // Assuming you have width and height from somewhere, like the initial image
                                // For demonstration, let's assume you have them
                                // You might need to get them from the initial image response or pass them as parameters

                                return getPoint(gridNumber, width, height, pointNumber);
                            }
                        }
                    }
                }
            }
        }
        Map<String, Object> errorResult = new HashMap<>();
        errorResult.put("x", null);
        errorResult.put("y", null);
        errorResult.put("error", "Not Found");
        return errorResult;
    }

    private final String template = """
            {
                "system_instruction": {
                    "parts": {
                        "text": "%s"
                    }
                },
                "tools": [
                    {
                        "function_declarations": [
                            {
                                "name": "%s",
                                "description": "%s",
                                "parameters": {
                                    "type": "object",
                                    "properties": {
                                        "%s": {
                                            "type": "number",
                                            "description": "%s"
                                        }
                                    },
                                    "required": [
                                        "%s"
                                    ]
                                }
                            }
                        ]
                    }
                ],
                "tool_config": {
                    "function_calling_config": {
                        "mode": "any"
                    }
                },
                "contents": [
                    {
                        "parts": [
                            {
                                "text": "%s"
                            },
                            {
                                "text": "%s"
                            },
                            {
                                "inline_data": {
                                    "mime_type": "%s",
                                    "data": "%s"
                                }
                            }
                        ]
                    }
                ]
            }
            """;
}
