package run.mone.mcp.hammerspoon.function.trigertrade;

import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import com.tigerbrokers.stock.openapi.client.config.ClientConfig;
import com.tigerbrokers.stock.openapi.client.https.client.TigerHttpClient;
import com.tigerbrokers.stock.openapi.client.https.domain.option.item.OptionChainItem;
import com.tigerbrokers.stock.openapi.client.https.domain.option.item.OptionRealTimeQuote;
import com.tigerbrokers.stock.openapi.client.https.domain.option.item.OptionRealTimeQuoteGroup;
import com.tigerbrokers.stock.openapi.client.https.domain.option.model.OptionChainFilterModel;
import com.tigerbrokers.stock.openapi.client.https.domain.option.model.OptionChainModel;
import com.tigerbrokers.stock.openapi.client.https.domain.trade.item.PrimeAssetItem;
import com.tigerbrokers.stock.openapi.client.https.request.option.OptionChainQueryV3Request;
import com.tigerbrokers.stock.openapi.client.https.request.quote.QuoteDelayRequest;
import com.tigerbrokers.stock.openapi.client.https.request.quote.QuoteMarketRequest;
import com.tigerbrokers.stock.openapi.client.https.request.quote.QuoteRealTimeQuoteRequest;
import com.tigerbrokers.stock.openapi.client.https.request.trade.PositionsRequest;
import com.tigerbrokers.stock.openapi.client.https.request.trade.PrimeAssetRequest;
import com.tigerbrokers.stock.openapi.client.https.response.option.OptionChainResponse;
import com.tigerbrokers.stock.openapi.client.https.response.quote.QuoteDelayResponse;
import com.tigerbrokers.stock.openapi.client.https.response.quote.QuoteMarketResponse;
import com.tigerbrokers.stock.openapi.client.https.response.quote.QuoteRealTimeQuoteResponse;
import com.tigerbrokers.stock.openapi.client.https.response.trade.PositionsResponse;
import com.tigerbrokers.stock.openapi.client.https.response.trade.PrimeAssetResponse;
import com.tigerbrokers.stock.openapi.client.struct.enums.Category;
import com.tigerbrokers.stock.openapi.client.struct.enums.Currency;
import com.tigerbrokers.stock.openapi.client.struct.enums.Market;
import com.tigerbrokers.stock.openapi.client.struct.enums.SecType;
import com.tigerbrokers.stock.openapi.client.util.builder.AccountParamBuilder;
import lombok.extern.slf4j.Slf4j;
import run.mone.mcp.hammerspoon.function.trigertrade.dto.OptionDetailBO;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Parser for option chain data from Tiger Brokers API
 *
 * @author shanwb
 * @author goodjava@qq.com
 * @date 2025-03-10
 */
@Slf4j
public class TigerTradeSdkUtil {

    private static ClientConfig clientConfig = ClientConfig.DEFAULT_CONFIG;
    private static TigerHttpClient client;

    private static Gson gson = new Gson();

    static {
        //从开发者信息页面导出的配置文件tiger_openapi_config.properties、tiger_openapi_token.properties存放路径
        clientConfig.configFilePath = System.getenv("TIGER_PATH");
        // clientConfig.secretKey = "xxxxxx"; // 机构账号交易员必填字段 secret key
        client = TigerHttpClient.getInstance().clientConfig(clientConfig);
    }


    private static String account = "21549496269944832";

    //账户持仓
    public static String positionsRequest() {
        PositionsRequest request = new PositionsRequest();

        String bizContent = AccountParamBuilder.instance()
                .account("21549496269944832")
                .secType(SecType.OPT)
                .buildJson();
//        request.setTigerId("20155232");
        request.setBizContent(bizContent);

        PositionsResponse response = client.execute(request);
        System.out.println(response);
        return "";
    }

    //综合/模拟账号获取资产
    public static PrimeAssetItem.CurrencyAssets getAssetByCurrency(Currency currency) {
        PrimeAssetRequest assetRequest = PrimeAssetRequest.buildPrimeAssetRequest(account, currency);
        assetRequest.setConsolidated(Boolean.TRUE);
        PrimeAssetResponse primeAssetResponse = client.execute(assetRequest);
        //查询证券相关资产信息
        PrimeAssetItem.Segment segment = primeAssetResponse.getSegment(Category.S);
        log.info("segment: " + JSONObject.toJSONString(segment));
        //查询账号中美元相关资产信息
        if (segment != null) {
            PrimeAssetItem.CurrencyAssets assetByCurrency = segment.getAssetByCurrency(Currency.USD);
            log.info("assetByCurrency: " + JSONObject.toJSONString(assetByCurrency));
            return assetByCurrency;
        }
        return null;
    }

    //QuoteMarketRequest (获取市场状态)
    public static QuoteMarketResponse quoteMarketRequest(Market market) {
        TigerHttpClient client = TigerHttpClient.getInstance().clientConfig(
                ClientConfig.DEFAULT_CONFIG);
        QuoteMarketResponse response = client.execute(QuoteMarketRequest.newRequest(market));
        if (response.isSuccess()) {
            log.info(Arrays.toString(response.getMarketItems().toArray()));
        } else {
            log.error("response error:" + response.getMessage());
        }
        return response;
    }

    //QuoteDelayRequest 获取股票延时行情(只支持美股)
    public static QuoteDelayResponse quoteDelayRequest(List<String> symbols) {
//        symbols.add("MIU.HK");
        QuoteDelayRequest delayRequest = QuoteDelayRequest.newRequest(symbols);
        QuoteDelayResponse response = client.execute(delayRequest);
        log.info("quoteDelayRequest {}", response);
        return response;
    }

    //获取实时行情 QuoteRealTimeQuoteRequest
    public static QuoteRealTimeQuoteResponse quoteRealTimeQuoteRequest(List<String> symbols) {
        QuoteRealTimeQuoteResponse response = client.execute(QuoteRealTimeQuoteRequest.newRequest(symbols, true));
        if (response.isSuccess()) {
            log.info(Arrays.toString(response.getRealTimeQuoteItems().toArray()));
        } else {
            log.error("response error:" + response.getMessage());
        }
        return response;
    }


    public static List<OptionDetailBO> getOptionChainDetail(OptionChainModel optionChainModel, String optionType) {
        OptionChainFilterModel filterModel = new OptionChainFilterModel()
                .inTheMoney(true)
                .impliedVolatility(0.1537, 0.8282)
                .openInterest(10, 50000)
                ;
        OptionChainQueryV3Request request = OptionChainQueryV3Request.of(optionChainModel, filterModel, Market.US);

        OptionChainResponse response = client.execute(request);

        List<OptionDetailBO> optionDetailBOList = parseOptionChainResponse(response);

        if (null != optionType) {
            return optionDetailBOList.stream().filter(o -> optionType.equalsIgnoreCase(o.getOptionType())).toList();
        } else {
            return optionDetailBOList;
        }
    }


    /**
     * Parse the OptionChainResponse into a list of OptionDetailBO objects
     *
     * @param response The response from the API
     * @return List of OptionDetailBO objects
     */
    private static List<OptionDetailBO> parseOptionChainResponse(OptionChainResponse response) {
        List<OptionDetailBO> result = new ArrayList<>();
        
        if (response == null || !response.isSuccess() || response.getOptionChainItems() == null) {
            return result;
        }
        
        for (OptionChainItem chainItem : response.getOptionChainItems()) {
            String symbol = chainItem.getSymbol();
            // Convert timestamp to date string (yyyy-MM-dd)
            String expiry = new SimpleDateFormat("yyyy-MM-dd")
                    .format(new Date(chainItem.getExpiry()));
            
            if (chainItem.getItems() != null) {
                for (OptionRealTimeQuoteGroup item : chainItem.getItems()) {
                    // Process call options
                    if (item.getCall() != null) {
                        OptionDetailBO callOption = createOptionDetailBO(item.getCall(), "call", expiry);
                        result.add(callOption);
                    }
                    
                    // Process put options
                    if (item.getPut() != null) {
                        OptionDetailBO putOption = createOptionDetailBO(item.getPut(), "put", expiry);
                        result.add(putOption);
                    }
                }
            }
        }
        
        return result;
    }

    /**
     * Create an OptionDetailBO from an option item
     * @param option The option item from the response
     * @param optionType The type of option (call or put)
     * @param expiry The expiry date
     * @return An OptionDetailBO object
     */
    private static OptionDetailBO createOptionDetailBO(OptionRealTimeQuote option, String optionType, String expiry) {
        OptionDetailBO detailBO = new OptionDetailBO();
        
        detailBO.setIdentifier(option.getIdentifier());
        detailBO.setOptionType(optionType);
        detailBO.setExpiry(expiry);
        detailBO.setStrike(option.getStrike());
        detailBO.setRight(option.getRight());
        detailBO.setBidPrice(option.getBidPrice());
        detailBO.setBidSize(option.getBidSize());
        detailBO.setAskPrice(option.getAskPrice());
        detailBO.setAskSize(option.getAskSize());
        detailBO.setVolume(option.getVolume());
        detailBO.setLatestPrice(option.getLatestPrice());
        detailBO.setPreClose(option.getPreClose());
        detailBO.setOpenInterest(option.getOpenInterest());
        detailBO.setMultiplier(option.getMultiplier());
        detailBO.setLastTimestamp(option.getLastTimestamp());
        detailBO.setImpliedVol(option.getImpliedVol());
        detailBO.setDelta(option.getDelta());
        detailBO.setGamma(option.getGamma());
        detailBO.setTheta(option.getTheta());
        detailBO.setVega(option.getVega());
        detailBO.setRho(option.getRho());
        
        return detailBO;
    }
} 