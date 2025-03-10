package run.mone.mcp.hammerspoon;

import com.google.gson.Gson;
import com.tigerbrokers.stock.openapi.client.config.ClientConfig;
import com.tigerbrokers.stock.openapi.client.https.client.TigerHttpClient;
import com.tigerbrokers.stock.openapi.client.https.domain.option.model.OptionChainFilterModel;
import com.tigerbrokers.stock.openapi.client.https.domain.option.model.OptionChainModel;
import com.tigerbrokers.stock.openapi.client.https.request.option.OptionChainQueryV3Request;
import com.tigerbrokers.stock.openapi.client.https.request.option.OptionExpirationQueryRequest;
import com.tigerbrokers.stock.openapi.client.https.response.option.OptionChainResponse;
import com.tigerbrokers.stock.openapi.client.https.response.option.OptionExpirationResponse;
import com.tigerbrokers.stock.openapi.client.struct.enums.Market;
import com.tigerbrokers.stock.openapi.client.struct.enums.TimeZoneId;
import org.junit.Test;
import run.mone.mcp.hammerspoon.function.trigertrade.dto.OptionDetailBO;
import run.mone.mcp.hammerspoon.function.trigertrade.TigerTradeSdkUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author shanwb
 * @date 2025-03-10
 */
public class TrigerTradeApiTest {

    private static ClientConfig clientConfig = ClientConfig.DEFAULT_CONFIG;
    private static TigerHttpClient client;

    private static Gson gson = new Gson();

    static {
        //从开发者信息页面导出的配置文件tiger_openapi_config.properties、tiger_openapi_token.properties存放路径
        // clientConfig.secretKey = "xxxxxx"; // 机构账号交易员必填字段 secret key
        client = TigerHttpClient.getInstance().clientConfig(clientConfig);
    }


    @Test
    public void testGetOptions() {

        List<String> symbols = new ArrayList<>();
        symbols.add("TSLA");
        OptionExpirationResponse response = client.execute(
                new OptionExpirationQueryRequest(symbols, Market.US));
        if (response.isSuccess()) {
            System.out.println(new Gson().toJson(response));
        } else {
            System.out.println("response error:" + response.getMessage());
        }
    }

    @Test
    public void testGetOptionsChain() {
        OptionChainModel basicModel = new OptionChainModel("TSLA", "2025-03-14", TimeZoneId.NewYork);

        List<OptionDetailBO> optionDetailBOList = TigerTradeSdkUtil.getOptionChainDetail(basicModel, "put");

        System.out.println(gson.toJson(optionDetailBOList));
    }

}
