package run.mone.mcp.hammerspoon.function.trigertrade;

import com.google.gson.Gson;
import com.tigerbrokers.stock.openapi.client.config.ClientConfig;
import com.tigerbrokers.stock.openapi.client.https.client.TigerHttpClient;
import com.tigerbrokers.stock.openapi.client.https.domain.option.item.OptionChainItem;
import com.tigerbrokers.stock.openapi.client.https.domain.option.item.OptionRealTimeQuote;
import com.tigerbrokers.stock.openapi.client.https.domain.option.item.OptionRealTimeQuoteGroup;
import com.tigerbrokers.stock.openapi.client.https.domain.option.model.OptionChainFilterModel;
import com.tigerbrokers.stock.openapi.client.https.domain.option.model.OptionChainModel;
import com.tigerbrokers.stock.openapi.client.https.request.option.OptionChainQueryV3Request;
import com.tigerbrokers.stock.openapi.client.https.response.option.OptionChainResponse;
import com.tigerbrokers.stock.openapi.client.struct.enums.Market;
import run.mone.mcp.hammerspoon.function.trigertrade.dto.OptionDetailBO;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Parser for option chain data from Tiger Brokers API
 * 
 * @author shanwb
 * @date 2025-03-10
 */
public class TigerTradeSdkUtil {

    private static ClientConfig clientConfig = ClientConfig.DEFAULT_CONFIG;
    private static TigerHttpClient client;

    private static Gson gson = new Gson();

    static {
        //从开发者信息页面导出的配置文件tiger_openapi_config.properties、tiger_openapi_token.properties存放路径
        clientConfig.configFilePath = "/Users/shanwenbang/Downloads/tiger/";
        // clientConfig.secretKey = "xxxxxx"; // 机构账号交易员必填字段 secret key
        client = TigerHttpClient.getInstance().clientConfig(clientConfig);
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