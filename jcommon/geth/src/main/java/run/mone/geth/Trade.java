package run.mone.geth;

import lombok.extern.slf4j.Slf4j;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.parity.Parity;
import run.mone.geth.bo.TradeResult;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;

/**
 * @author goodjava@qq.com
 * @date 2022/6/17 16:43
 */
@Slf4j
public class Trade {

    private static BigInteger nonce = new BigInteger("0");

    private static BigInteger gasPrice = new BigInteger("1");

    private static BigInteger gasLimit = new BigInteger("50");

    private Parity parity = ParityClient.getParity();

    public TradeResult trasfer(String accountId, String passsword, String toAccountId, BigInteger amount) {

        Transaction transaction = Transaction.createEtherTransaction(accountId, null, null, null, toAccountId, amount);
        TradeResult tr = new TradeResult();
        tr.setSender(accountId);
        tr.setReceiver(toAccountId);
        tr.setAmount(amount);
        tr.setTime(new Date());
        try {
            EthSendTransaction ethSendTransaction = parity.personalSendTransaction(transaction, passsword).send();
            if (ethSendTransaction != null) {
                if (ethSendTransaction.getError() != null) {
                    tr.setMsg(ethSendTransaction.getError().getMessage());
                    tr.setRes(false);
                    System.out.println(ethSendTransaction.getError().getMessage());
                } else {
                    String tradeHash = ethSendTransaction.getTransactionHash();
                    tr.setTrade_id(tradeHash);
                    tr.setMsg("success");
                    tr.setRes(true);
                    log.info("账户:[{}]转账到账户:[{}],交易hash:[{}]", accountId, toAccountId, tradeHash);
                }
            }
        } catch (Exception e) {
            tr.setMsg(e.getMessage());
            tr.setRes(false);
            log.error("账户:[{}]交易失败!", accountId, e);
        }
        return tr;
    }


}
