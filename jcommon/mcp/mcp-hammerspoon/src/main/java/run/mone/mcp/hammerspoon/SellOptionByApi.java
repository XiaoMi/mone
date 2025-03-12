package run.mone.mcp.hammerspoon;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.tigerbrokers.stock.openapi.client.https.domain.contract.item.ContractItem;
import com.tigerbrokers.stock.openapi.client.https.domain.option.model.OptionChainModel;
import com.tigerbrokers.stock.openapi.client.https.request.trade.QueryOrderRequest;
import com.tigerbrokers.stock.openapi.client.https.request.trade.TradeOrderRequest;
import com.tigerbrokers.stock.openapi.client.https.response.trade.BatchOrderResponse;
import com.tigerbrokers.stock.openapi.client.https.response.trade.TradeOrderResponse;
import com.tigerbrokers.stock.openapi.client.struct.enums.ActionType;
import com.tigerbrokers.stock.openapi.client.struct.enums.MethodName;
import com.tigerbrokers.stock.openapi.client.struct.enums.SecType;
import com.tigerbrokers.stock.openapi.client.struct.enums.TimeZoneId;
import com.tigerbrokers.stock.openapi.client.util.builder.AccountParamBuilder;
import run.mone.hive.configs.LLMConfig;
import run.mone.hive.llm.LLM;
import run.mone.hive.llm.LLMProvider;
import run.mone.mcp.hammerspoon.function.trigertrade.TigerTradeSdkUtil;
import run.mone.mcp.hammerspoon.function.trigertrade.dto.OptionDetailBO;

import java.util.List;

/**
 * @author shanwb
 * @date 2025-03-11
 */
public class SellOptionByApi {

    public static Gson gson = new Gson();


    public static void main(String[] args) {
        // 1. Get option chain details to find a suitable put option
        OptionChainModel basicModel = new OptionChainModel("TSLA", "2025-03-14", TimeZoneId.NewYork);
        List<OptionDetailBO> putOptions = TigerTradeSdkUtil.getOptionChainDetail(basicModel, "put");

        if (putOptions == null || putOptions.isEmpty()) {
            System.out.println("No put options available for the specified date");
            return;
        }

        /**
         * 特斯拉、meta、苹果、谷歌、英伟达
         *
         * prompt要点：
         * - 前提分析（本质:多少钱打算挣多少）
         *      - 先考虑年化收益率
         *          银行1.8%
         *      - 本金（60~80w最佳操作）
         *
         * - 只看周的
         * - 分析支撑位
         *   如果离的很近
         *   如果离的很远，可以往上靠的
         * - 分析大盘趋势
         *   如果大盘向下走：多给一些安全垫
         *   如果大盘向上走：
         * - 参考账户余额
         *
         */


        String prompt = "作为一位专业的期权卖方策略分析师，您的任务是帮助我分析当前的期权链数据，并为我推荐最佳的卖出看跌期权(PUT)交易机会。\n" +
                "\n" +
                "## 您需要的信息\n" +
                "1. 标的资产当前价格\n" +
                "2. 标的资产的基本面分析摘要(可选但推荐)\n" +
                "3. 标的资产的技术面分析摘要(可选但推荐)\n" +
                "4. 当前波动率环境(VIX或隐含波动率水平)\n" +
                "5. 期权链数据，包括：\n" +
                "   - 到期日\n" +
                "   - 行权价\n" +
                "   - 看跌期权价格(bid/ask)\n" +
                "   - 隐含波动率\n" +
                "   - 期权希腊值(Delta, Gamma, Theta, Vega)\n" +
                "   - 未平仓合约数量(可选)\n" +
                "\n" +
                "## 分析流程\n" +
                "基于上述信息，请执行以下分析:\n" +
                "\n" +
                "### 1. 筛选标准\n" +
                "- 分析不同到期日的风险收益比\n" +
                "- 评估不同行权价位的安全边际\n" +
                "- 计算各期权的年化收益率\n" +
                "- 考虑隐含波动率相对历史水平的位置\n" +
                "- 评估每个期权合约的流动性\n" +
                "\n" +
                "### 2. 风险评估\n" +
                "- 计算最大可能亏损\n" +
                "- 评估被指派的可能性\n" +
                "- 分析如果标的物大幅下跌时的风险暴露\n" +
                "- 考虑到期前可能出现的重大事件(财报、分红等)\n" +
                "\n" +
                "### 3. 策略建议\n" +
                "根据以上分析，请推荐：\n" +
                "- 一个最佳的卖出看跌期权交易，包括具体到期日和行权价\n" +
                "- 止损建议\n" +
                "- 盈利目标(何时平仓)\n" +
                "- 可能的调整策略(如果市场转向不利)\n" +
                "\n" +
                "### 4. 收益计算\n" +
                "- 提供保证金要求估算\n" +
                "- 计算潜在收益率(占用保证金的百分比)\n" +
                "- 计算年化收益率\n" +
                "- 分析盈亏平衡点\n" +
                "\n" +
                "## 输出格式\n" +
                "请以json形式返回(主要不要用```或```json修饰)，格式如下：\n" +
                "\n" +
                "{\n" +
                "  \"identifier\":\"请严格从我给你的期权链数据中选取\",\n" +
                "  \"strike\":\"行权价\",\n" +
                "  \"bidPrice\":\"买盘价\",\n" +
                "  \"askPrice\":\"卖盘价\",\n" +
                "  \"description\":\"决策描述\"\n" +
                "}\n" +
                "\n" +
                "\n" +
                "## 特别注意事项\n" +
                "- 优先考虑风险管理而非单纯追求高收益\n" +
                "- 考虑当前市场环境和波动率情况\n" +
                "- 评估期权的流动性(未平仓合约数量和bid-ask差价)\n" +
                "- 考虑任何即将到来的可能影响标的资产价格的事件\n" +
                "\n" +
                "\n" +
                "需要的信息如下：\n" +
                "1.标的资产当前价格:%s\n" +
                "2.期权链数据:%s \n" +
                "请按上面的要求，返回json结果.";
        prompt = String.format(prompt, "222.15", gson.toJson(putOptions));

        System.out.println("AI 分析 prompt:" + prompt);

        LLM vllm = new LLM(LLMConfig.builder().llmProvider(LLMProvider.DOUBAO_DEEPSEEK_V3).build());
        String res = vllm.chat(prompt);
        System.out.println("AI 分析 结果:" + res);

        // Extract identifier from LLM response
        String identifier = null;
        try {
            JsonObject jsonResponse = gson.fromJson(res, JsonObject.class);
            identifier = jsonResponse.get("identifier").getAsString();
            System.out.println("Extracted identifier: " + identifier);
        } catch (Exception e) {
            System.out.println("Error parsing LLM response: " + e.getMessage());
            return;
        }

        // Find the selected option in the list based on the identifier
        OptionDetailBO selectedOption = null;
        for (OptionDetailBO option : putOptions) {
            if (option.getIdentifier().equals(identifier)) {
                selectedOption = option;
                break;
            }
        }

        if (selectedOption == null) {
            System.out.println("Could not find option with identifier: " + identifier);
            return;
        }

        System.out.println("Selected put option: " + gson.toJson(selectedOption));

        // 3. Create an order to sell 1 contract of the selected put option
        try {
            ContractItem contract = ContractItem.buildOptionContract(selectedOption.getIdentifier());
            contract.setAccount("21549496269944832");

            TradeOrderRequest request = TradeOrderRequest.buildLimitOrder(contract, ActionType.SELL, 1, selectedOption.getBidPrice());
            //TradeOrderRequest request = TradeOrderRequest.buildMarketOrder(contract, ActionType.SELL, 1);
            TradeOrderResponse response = TigerTradeSdkUtil.execute(request);
            System.out.println("sell put response:" + new Gson().toJson(response));
            System.out.println("end.....identifier:" + selectedOption.getIdentifier() + ", price:" + selectedOption.getBidPrice());

            // For testing purposes, we're just printing the order details without actually placing it
            //System.out.println("This is a test - no actual order was placed");

//            System.out.println("订单情况：");
//            QueryOrderRequest request1 = new QueryOrderRequest(MethodName.FILLED_ORDERS);
//
//            String bizContent = AccountParamBuilder.instance()
//                    .account("21549496269944832")
//                    .secType(SecType.OPT)
//                    .startDate("2025-03-09 22:34:30")
//                    .endDate("2025-03-11 22:50:31")
//                    .buildJson();
//
//            request1.setBizContent(bizContent);
//            BatchOrderResponse response1 = TigerTradeSdkUtil.execute(request1);
//            System.out.println(new Gson().toJson(response1));
//
//            System.out.println("------------------");

        } catch (Exception e) {
            System.out.println("Error preparing option order: " + e.getMessage());
            e.printStackTrace();
        }
    }

}
