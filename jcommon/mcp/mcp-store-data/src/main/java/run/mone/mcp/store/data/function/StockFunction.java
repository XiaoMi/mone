package run.mone.mcp.store.data.function;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.Data;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.m78.client.util.GsonUtils;
import run.mone.mcp.store.data.domain.Stock;
import run.mone.mcp.store.data.service.StoreDataService;

@Data
@Component
public class StockFunction implements Function<Map<String, Object>, McpSchema.CallToolResult> {

    @Autowired
    private StoreDataService storeDataService;

    private String name = "stock";

    private String desc = "根据商品skuId和门店ID获取库存信息";

    private String ideaPort;

    private String toolScheme = """
            {
                "type": "object",
                "properties": {
                    "skuId": {
                        "type": "string",
                        "description":"商品的skuId"
                    },
                    "storeId": {
                        "type": "string",
                        "description":"门店ID"
                    }
                },
                "required": ["skuId", "storeId"]
            }
            """;

    @Override
    public McpSchema.CallToolResult apply(Map<String, Object> arguments) {
        try {
            String skuId = (String) arguments.get("skuId");
            String storeId = (String) arguments.get("storeId");

            System.out.println("StockFunction skuId : " + skuId + " storeId : " + storeId);

            Stock stock = storeDataService.getStock(skuId, storeId);

            String result = GsonUtils.GSON.toJson(stock);

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