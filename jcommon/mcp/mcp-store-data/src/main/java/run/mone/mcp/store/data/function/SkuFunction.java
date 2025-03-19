package run.mone.mcp.store.data.function;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.Data;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.m78.client.util.GsonUtils;
import run.mone.mcp.store.data.domain.Sku;
import run.mone.mcp.store.data.service.StoreDataService;

@Data
@Component
public class SkuFunction implements Function<Map<String, Object>, McpSchema.CallToolResult> {

    @Autowired
    private StoreDataService storeDataService;

    private String name = "sku";

    private String desc = "根据文字描述获取小米商品sku。注意这里只需要从聊天会话中获取小米产品型号即可，比如小米15";

    private String ideaPort;

    private String toolScheme = """
            {
                "type": "object",
                "properties": {
                    "productModel": {
                        "type": "string",
                        "description":"从会话中提取到的小米产品型号"
                    }
                },
                "required": ["productModel"]
            }
            """;

    @Override
    public McpSchema.CallToolResult apply(Map<String, Object> arguments) {
        try {
            String productModel = (String) arguments.get("productModel");
            List<Sku> skuList = storeDataService.getSku(productModel);

            String result = GsonUtils.GSON.toJson(skuList);
            
            return new McpSchema.CallToolResult(
                List.of(new McpSchema.TextContent(result)), 
                false
            );
        } catch (Exception e) {
            return new McpSchema.CallToolResult(
                List.of(new McpSchema.TextContent("Error: " + e.getMessage())), 
                true
            );
        }
    }
}
