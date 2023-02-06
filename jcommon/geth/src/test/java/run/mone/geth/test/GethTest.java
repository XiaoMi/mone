package run.mone.geth.test;

import lombok.SneakyThrows;
import org.junit.Test;
import org.web3j.protocol.Web3j;
//import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterNumber;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.methods.response.EthBlockNumber;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.http.HttpService;
import org.web3j.protocol.parity.Parity;
import run.mone.geth.Geth;
import run.mone.geth.Trade;
import run.mone.geth.bo.Tran;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * @author goodjava@qq.com
 * @date 2022/6/17 16:20
 */
public class GethTest {

    /**
     * 连接本地的区块链服务
     */
    private String gethClientUrl = "http://127.0.0.1:8545";

//    private Admin initAdmin() {
//        return Admin.build(getService());
//    }

    public void testPeers() {
    }


    @Test
    public void testTrade() {
        Trade trade = new Trade();
        trade.trasfer("0x1f500279c50c2310034ddc3d35ccaad242ad9a5c","","0xe40c701b9847838644ebeda2aa222ef39acd46a7",new BigInteger("1000000000000000000"));
    }


    @SneakyThrows
    @Test
    public void testGetBalance() {
        String accountId = "0x1f500279c50c2310034ddc3d35ccaad242ad9a5c";
        Parity parity = initParity();
        DefaultBlockParameter defaultBlockParameter = new DefaultBlockParameterNumber(3893);
        EthGetBalance ethGetBalance = parity.ethGetBalance(accountId, defaultBlockParameter).send();
        if (ethGetBalance != null) {
            System.out.println(ethGetBalance.getBalance());
        }
    }


    /**
     * 查询block数量
     */
    @Test
    public void testBlockNumber() {
        Web3j web3j = initWeb3j();
        Request<?, EthBlockNumber> request = web3j.ethBlockNumber();
        try {
            System.out.println(request.send().getBlockNumber());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 查询本地账户数量
     */
    @Test
    public void testIds() {
        Parity parity = initParity();
        List<String> ids = new ArrayList<>();
        try {
            ids = parity.personalListAccounts().send().getAccountIds();
            System.out.println("用户数量 : " + ids.size());
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(ids);

    }

    private Parity initParity() {
        return Parity.build(getService());
    }


    private Web3j initWeb3j() {
        return Web3j.build(getService());
    }

    private HttpService getService() {
        return new HttpService(gethClientUrl);
    }

    @Test
    public void testTran() {
        List<Tran> res = new Geth().getTransaction(t ->t.getFrom().equals("0x1f500279c50c2310034ddc3d35ccaad242ad9a5c"));
        System.out.println(" ======== ============================");
        System.out.println(res);
    }
}
