package run.mone.mcp.hammerspoon.function.tigertrade.function;

import com.tigerbrokers.stock.openapi.client.https.domain.option.model.OptionChainModel;
import com.tigerbrokers.stock.openapi.client.struct.enums.Market;
import com.tigerbrokers.stock.openapi.client.struct.enums.TimeZoneId;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.mcp.hammerspoon.function.tigertrade.service.TradeService;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Function to sell put options with streaming progress updates
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SellPutOptionFunction implements Function<Map<String, Object>, Flux<McpSchema.CallToolResult>> {

    private final TradeService tradeService;

    public String getName() {
        return "stream_sellPutOption";
    }

    public String getDesc() {
        return "Sell put options with real-time progress updates through a reactive stream.";
    }

    public String getToolScheme() {
        return """
                {
                    "type": "object",
                    "properties": {
                        "symbol": {
                            "type": "string",
                            "description": "The stock symbol (e.g., TSLA, AAPL 默认值是TSLA)"
                        },
                        "market": {
                            "type": "string",
                            "enum": ["US", "HK", "CN"],
                            "description": "Market where the option is traded (默认值是 US)"
                        }
                    },
                    "required": ["symbol", "market"]
                }
                """;
    }

    @Override
    public Flux<McpSchema.CallToolResult> apply(Map<String, Object> arguments) {
        String expiryDate = new SimpleDateFormat().format("yyyy-MM-dd");
        Market market = Market.US;
        OptionChainModel optionChainModel = new OptionChainModel("TSLA", expiryDate, TimeZoneId.NewYork);

        return tradeService.sellPutOption(optionChainModel, market, expiryDate)
                .map(message -> new McpSchema.CallToolResult(
                        List.of(new McpSchema.TextContent(message)),
                        false
                ))
                .onErrorResume(error -> {
                    log.error("Error in sellPutOption", error);
                    return Flux.just(new McpSchema.CallToolResult(
                            List.of(new McpSchema.TextContent("Error: " + error.getMessage())),
                            true
                    ));
                });
    }
} 