package run.mone.mcp.hammerspoon.function.tigertrade.service;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.tigerbrokers.stock.openapi.client.https.domain.trade.item.PrimeAssetItem;
import com.tigerbrokers.stock.openapi.client.struct.enums.Currency;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

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
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import run.mone.hive.configs.LLMConfig;
import run.mone.hive.llm.LLM;
import run.mone.hive.llm.LLMProvider;
import run.mone.mcp.hammerspoon.function.tigertrade.TigerTradeSdkUtil;
import run.mone.mcp.hammerspoon.function.tigertrade.dto.OptionDetailBO;
import run.mone.mcp.hammerspoon.function.tigertrade.utils.MessageUtils;
import run.mone.mcp.hammerspoon.function.tigertrade.utils.PromptFileUtils;
import run.mone.mcp.hammerspoon.function.tigertrade.utils.TemplateUtils;

/**
 * @author shanwb
 * @author goodjava@qq.com
 * @date 2025-03-12
 */
@Component
@Slf4j
public class TradeService {

    private static Gson gson = new Gson();

    public Flux<String> sellPutOption(OptionChainModel optionChainModel, Market market, String optionDate) {
        return Flux.create(sink -> {
            sellPutOption(optionChainModel, market, optionDate, sink, 1);
            //结束
            sink.complete();
        });
    }


    public TradeOrderResponse sellPutOption(OptionChainModel optionChainModel, Market market, String optionDate, FluxSink<String> sink, int quantity) {
        try {
            MessageUtils.sendMessage(sink, "开始卖put  信息: " + optionChainModel + " market:" + market);

            Map<String, Object> templateParams = new HashMap<>();
            PrimeAssetItem.CurrencyAssets currencyAssets = TigerTradeSdkUtil.getAssetByCurrency(Currency.USD);
            log.info("currencyAssets:{}", gson.toJson(currencyAssets));


            //1.查询期权链
            MessageUtils.sendMessage(sink, "查询期权链");
            List<OptionDetailBO> putOptions = TigerTradeSdkUtil.getOptionChainDetail(optionChainModel, "put", market);
            Preconditions.checkArgument(!CollectionUtils.isEmpty(putOptions), String.format("No put options available for the specified date:%s", optionDate));

            MessageUtils.sendMessage(sink, "查询股票行情");
            //2.查询股票行情
            QuoteDelayResponse quoteDelayResponse = TigerTradeSdkUtil.quoteDelayRequest(Arrays.asList(optionChainModel.getSymbol()));
            String quoteDelayResponseStr = gson.toJson(quoteDelayResponse);
            log.info("quoteDelayResponse:{}", quoteDelayResponseStr);
            MessageUtils.sendMessage(sink, "股票信息:" + quoteDelayResponseStr);

            QuoteDelayItem quoteDelayItem = null;
//            String stockQuotePrompt = null;
            List<QuoteDelayItem> quoteDelayItemList = quoteDelayResponse.getQuoteDelayItems();
            if (!CollectionUtils.isEmpty(quoteDelayItemList)) {
                quoteDelayItem = quoteDelayItemList.getFirst();
//                String stockQuotePromptTemplate = PromptFileUtils.readPromptFile(PromptFileUtils.STOCK_QUOTE_PROMPT);
//                stockQuotePrompt = TemplateUtils.processTemplateContent(stockQuotePromptTemplate, quoteDelayItem);
//                log.info("stockQuotePrompt:{}", stockQuotePrompt);
            }

            transTemplateParams(templateParams, currencyAssets, putOptions, quoteDelayItem);

            MessageUtils.sendMessage(sink, "ai决策 选期权");
            //4.ai决策 选期权
            OptionDetailBO selectedOption = selectOptionByAi(templateParams, putOptions);
            Preconditions.checkArgument(null != selectedOption, "ai select option is null, break");

            //5.下单
            MessageUtils.sendMessage(sink, "下单:" + selectedOption.getIdentifier());

            ContractItem contract = ContractItem.buildOptionContract(selectedOption.getIdentifier());
            log.info("goto build order ...........");
            //TradeOrderRequest request = TradeOrderRequest.buildLimitOrder(contract, ActionType.SELL, 1, selectedOption.getBidPrice());
            TradeOrderRequest request = TradeOrderRequest.buildMarketOrder(contract, ActionType.SELL, quantity);
            TradeOrderResponse response = TigerTradeSdkUtil.execute(request);
            log.info("response:{}", new Gson().toJson(response));

            log.info("end.....identifier:{}, price:{}", selectedOption.getIdentifier(), selectedOption.getBidPrice());
            MessageUtils.sendMessage(sink, "下单结束 identifier:" + selectedOption.getIdentifier() + ", price:" + selectedOption.getBidPrice());
            return response;
        } catch (IOException | TigerApiException e) {
            log.error("sellPutOption exception:", e);
            throw new RuntimeException(e);
        }
    }

    private void transTemplateParams(final Map<String, Object> templateParams,
                                     PrimeAssetItem.CurrencyAssets currencyAssets,
                                     List<OptionDetailBO> putOptions,
                                     QuoteDelayItem quoteDelayItem) {
        BeanMap currencyAssetsMap = BeanMap.create(currencyAssets);
        BeanMap quoteDelayItemMap = BeanMap.create(quoteDelayItem);

        templateParams.put("optionChains", putOptions);
        templateParams.putAll(currencyAssetsMap);

        templateParams.put("stock", quoteDelayItem.getSymbol());
        templateParams.putAll(quoteDelayItemMap);
    }

    private static OptionDetailBO selectOptionByAi(Map<String, Object> templateParams, List<OptionDetailBO> putOptions) throws IOException {
        String sellPutPromptTemplate = PromptFileUtils.readPromptFile(PromptFileUtils.SELL_PUT_STRATEGY_PROMPT);
        String sellPutPrompt = TemplateUtils.processTemplateContent(sellPutPromptTemplate, templateParams);

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
