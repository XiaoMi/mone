package run.mone.mcp.store.data.function;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.Data;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.m78.client.util.GsonUtils;
import run.mone.mcp.store.data.domain.Stock;
import run.mone.mcp.store.data.service.StoreDataService;

@Data
@Component
@Slf4j
public class StockFunction implements Function<Map<String, Object>, McpSchema.CallToolResult> {

    @Autowired
    private StoreDataService storeDataService;

    private String name = "store_data_stock";

    private String desc = "根据商品skuId获取商品库存信息";

    private String ideaPort;

    private String toolScheme = """
            {
                "type": "object",
                "properties": {
                    "skuId": {
                        "type": "string",
                        "description":"商品的skuId"
                    }
                },
                "required": ["skuId"]
            }
            """;

    @Override
    public McpSchema.CallToolResult apply(Map<String, Object> arguments) {
        try {
            String skuId = (String) arguments.get("skuId");
            String storeId = (String) arguments.get("storeId");

            log.info("StockFunction skuId : " + skuId + " storeId : " + storeId);

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