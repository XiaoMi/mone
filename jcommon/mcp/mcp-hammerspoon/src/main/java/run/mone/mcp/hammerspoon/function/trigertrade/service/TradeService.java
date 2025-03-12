package run.mone.mcp.hammerspoon.function.trigertrade.service;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.tigerbrokers.stock.openapi.client.TigerApiException;
import com.tigerbrokers.stock.openapi.client.https.domain.contract.item.ContractItem;
import com.tigerbrokers.stock.openapi.client.https.domain.option.model.OptionChainModel;
import com.tigerbrokers.stock.openapi.client.https.domain.quote.item.QuoteDelayItem;
import com.tigerbrokers.stock.openapi.client.https.request.trade.TradeOrderRequest;
import com.tigerbrokers.stock.openapi.client.https.response.quote.QuoteDelayResponse;
import com.tigerbrokers.stock.openapi.client.https.response.trade.TradeOrderResponse;
import com.tigerbrokers.stock.openapi.client.struct.enums.ActionType;
import com.tigerbrokers.stock.openapi.client.struct.enums.Market;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import run.mone.hive.configs.LLMConfig;
import run.mone.hive.llm.LLM;
import run.mone.hive.llm.LLMProvider;
import run.mone.mcp.hammerspoon.function.trigertrade.TigerTradeSdkUtil;
import run.mone.mcp.hammerspoon.function.trigertrade.dto.OptionDetailBO;
import run.mone.mcp.hammerspoon.function.trigertrade.utils.PromptFileUtils;
import run.mone.mcp.hammerspoon.function.trigertrade.utils.TemplateUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author shanwb
 * @date 2025-03-12
 */
@Component
@Slf4j
public class TradeService {

    private static Gson gson  = new Gson();

    public TradeOrderResponse sellPutOption(OptionChainModel optionChainModel, Market market, String optionDate) {
        try {

            //1.查询期权链
            List<OptionDetailBO> putOptions = TigerTradeSdkUtil.getOptionChainDetail(optionChainModel, "put", market);
            Preconditions.checkArgument(!CollectionUtils.isEmpty(putOptions), String.format("No put options available for the specified date:%s", optionDate));

            //2.期权链 to markdown Prompt
            Map<String, Object> optionChains = new HashMap<>();
            optionChains.put("optionChains", putOptions);
            String optionChainPromptTemplate = PromptFileUtils.readPromptFile(PromptFileUtils.OPTION_CHAIN_PROMPT);
            String optionChainPrompt = TemplateUtils.processTemplateContent(optionChainPromptTemplate, optionChains);
            log.info("optionChainPrompt:{}", optionChainPrompt);

            //3.查询股票行情
            QuoteDelayResponse quoteDelayResponse = TigerTradeSdkUtil.quoteDelayRequest(Arrays.asList(optionChainModel.getSymbol()));
            log.info("quoteDelayResponse:{}", gson.toJson(quoteDelayResponse));
            QuoteDelayItem quoteDelayItem;
            String stockQuotePrompt = null;
            List<QuoteDelayItem> quoteDelayItemList = quoteDelayResponse.getQuoteDelayItems();
            if (!CollectionUtils.isEmpty(quoteDelayItemList)) {
                quoteDelayItem = quoteDelayItemList.getFirst();
                String stockQuotePromptTemplate = PromptFileUtils.readPromptFile(PromptFileUtils.STOCK_QUOTE_PROMPT);
                stockQuotePrompt = TemplateUtils.processTemplateContent(stockQuotePromptTemplate, quoteDelayItem);
                log.info("stockQuotePrompt:{}", stockQuotePrompt);
            }

            //4.ai决策 选期权
            OptionDetailBO selectedOption = selectOptionByAi(stockQuotePrompt, optionChainPrompt, putOptions);

            //5.下单
            ContractItem contract = ContractItem.buildOptionContract(selectedOption.getIdentifier());
            log.info("goto build order ...........");
            //TradeOrderRequest request = TradeOrderRequest.buildLimitOrder(contract, ActionType.SELL, 1, selectedOption.getBidPrice());
            TradeOrderRequest request = TradeOrderRequest.buildMarketOrder(contract, ActionType.SELL, 1);
            TradeOrderResponse response = TigerTradeSdkUtil.execute(request);
            log.info("response:{}", new Gson().toJson(response));
            log.info("end.....identifier:{}, price:{}", selectedOption.getIdentifier(), selectedOption.getBidPrice());

            return response;
        } catch (IOException | TigerApiException e) {
            log.error("sellPutOption exception:", e);
            throw new RuntimeException(e);
        }
    }

    private static OptionDetailBO selectOptionByAi(String stockQuotePrompt, String optionChainPrompt, List<OptionDetailBO> putOptions) throws IOException {
        Map<String, Object> sellPutPromptParams = new HashMap<>();
        sellPutPromptParams.put("stockQuote", stockQuotePrompt);
        sellPutPromptParams.put("optionChain", optionChainPrompt);
        String sellPutPromptTemplate = PromptFileUtils.readPromptFile(PromptFileUtils.SELL_PUT_STRATEGY_PROMPT);
        String sellPutPrompt = TemplateUtils.processTemplateContent(sellPutPromptTemplate, sellPutPromptParams);

        log.info("final sell put prompt:{}", sellPutPrompt);
        LLM vllm = new LLM(LLMConfig.builder().llmProvider(LLMProvider.DOUBAO_DEEPSEEK_V3).build());
        String res = vllm.chat(sellPutPrompt);
        log.info("sell put llm res:{}", res);

        // Extract identifier from LLM response
        String identifier = null;
        try {
            JsonObject jsonResponse = gson.fromJson(res, JsonObject.class);
            identifier = jsonResponse.get("identifier").getAsString();
            log.info("Extracted identifier: {}", identifier);
        } catch (Exception e) {
            log.error("Error parsing LLM response: ", e);
            return null;
        }

        // Find the selected option in the list based on the identifier
        OptionDetailBO selectedOption = null;
        for (OptionDetailBO option : putOptions) {
            if (option.getIdentifier().equals(identifier)) {
                selectedOption = option;
                break;
            }
        }

        return selectedOption;
    }


}
