package run.mone.geth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterNumber;
import org.web3j.protocol.core.Request;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthBlockNumber;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.http.HttpService;
import org.web3j.protocol.parity.Parity;
import run.mone.geth.bo.Tran;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author goodjava@qq.com
 * @date 2022/6/20 13:54
 */

@Slf4j
public class Geth {

    private String gethClientUrl = "http://127.0.0.1:80";

    @AllArgsConstructor
    @Data
    public static class Nmb {
        private String name;
        private int nmb;

        private String hashId;
    }

    private Parity initParity() {
        return Parity.build(getService());
    }

    private HttpService getService() {
        return new HttpService(gethClientUrl);
    }


    public List<Nmb> moneNmbInfo() {
        Map<String, String> m = new HashMap<>();
        m.put("0xd5ded03d104943ea6a191ce22581e3a5047349e5", "现金池");
        m.put("0x1f500279c50c2310034ddc3d35ccaad242ad9a5c", "zhangzhiyong1");
        m.put("0x5Fe6b7EC79A7326e202FDE21Beb3C7f03707a580", "dingpei");
        m.put("0x9083321a33ab3668d71b0c2d4e2c1d5edce79046", "dingtao");
        m.put("0xb86127e7ea0e6d8eda66ed0fa87d421670e0b637", "zhangxiaowei6");
        m.put("0x81bbc704cb4624b566a4042dd34444e41364eec8", "dongzhenxing");
        m.put("0x52cbc5b1b54a0d64c8a8ff749a16be596f1db5a5", "wangtao29");
        m.put("0x18e67224d29551543eddcebbb2115feee0b39d7f", "liuchuankang");
        m.put("0x113c9dd67809128a62ec5087570c5ee6468f4519", "shanwenbang");
        m.put("0xe40c701b9847838644ebeda2aa222ef39acd46a7", "renqingfu");
        m.put("0xa91dbaf08bd772aed28d483fd6df7637f12a415c", "gaoxihui");
        m.put("0x1e522050773430456fd226133b7d8025db7446b6", "caobaoyu");
        m.put("0x3e991be10de8bf84666f9341e66edfff1e293398","zhangping17");
        m.put("0x8b4019a6ef1164080da10bfbb54b093eb54d9984","gaoyulin");
        m.put("0x4c9115125ceb99b500338f46528d41bdbe7277cd","jiangzheng3");
        m.put("0xcA1C088462a23e3aB8c38C1A5Be2db60FbFd3169","wangyandong3");
        m.put("0xb6a2a733b20577c5678f09aeb8d52cdbf094e798","jinannan");
        m.put("0x1a3625565a1eea7d70db298b0a243f2eed5422d2","kangting1");
        m.put("0xfe9d264b6b499d89d2d677eb8bad0c826b18e89a","zhanggaofeng1");
        m.put("0x6dafd89c9a8e03a9d9a037bce59d897fec73a752","wangmin17");
        m.put("0xD26ad318e7C3b1cF2A8B959AF75918B275EeECcf","yangxirui");
        m.put("0xad038eaa64b60b795132f20d806e4bac0c84299c","wangzhidong1");
        m.put("0x4be2fb62f776d6326ee0dedfd17ad7866deff4ce","tanlinlin");
        m.put("0x9d7713634be087481bf4579f05c838b71b4375df","wanghaoyang");
        m.put("0x613cd2bb0744cd367f27bdc0e0aa6e01c175965c","shilu");
        m.put("0xabdb4150df3b9b936cfac3150782bec80d417da8","sunzekai");
        List<Nmb> list = new ArrayList<>();
        m.entrySet().forEach(entry -> {
            String accountId = entry.getKey();
            Parity parity = initParity();
            DefaultBlockParameter defaultBlockParameter = new DefaultBlockParameterNumber(blockNum());
            EthGetBalance ethGetBalance = null;
            try {
                ethGetBalance = parity.ethGetBalance(accountId, defaultBlockParameter).send();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (ethGetBalance != null) {
//                System.out.println(entry.getValue() + ":" + ethGetBalance.getBalance().divide(new BigInteger("1000000000000000000")));
                list.add(new Nmb(entry.getValue(), ethGetBalance.getBalance().divide(new BigInteger("1000000000000000000")).intValue(),entry.getKey()));
            }
        });
//        System.out.println(list);
        return list.stream().sorted((a, b) -> {
            if (a.nmb < b.nmb) {
                return 1;
            } else {
                return -1;
            }
        }).collect(Collectors.toList());
    }

    @SneakyThrows
    public List<Tran> getTransaction(Predicate<EthBlock.TransactionObject> predicate) {
        Web3j web3j = initWeb3j();
        List<Tran> list = new ArrayList<>();
        long num = blockNum();
        for (int i = 0; i < num; i++) {
            System.out.println(i);
            try {
                List<EthBlock.TransactionResult> txs = web3j.ethGetBlockByNumber(DefaultBlockParameter.valueOf(new BigInteger(i + "")), true).send().getBlock().getTransactions();
                txs.forEach(tx -> {
                    EthBlock.TransactionObject transaction = (EthBlock.TransactionObject) tx.get();
                    if (predicate.test(transaction)) {
                        log.info("----->" + transaction.getFrom() + ":" + transaction.getTo() + ":" + transaction.getValueRaw());
                        list.add(new Tran(transaction.getFrom(), transaction.getTo(), transaction.getValue().toString()+""));
                    }
                });
            } catch (Throwable ignore) {

            }
        }
        return list;
    }

    @SneakyThrows
    public long blockNum() {
        Web3j web3j = initWeb3j();
        Request<?, EthBlockNumber> request = web3j.ethBlockNumber();
        return request.send().getBlockNumber().longValue();
    }

    private Web3j initWeb3j() {
        return Web3j.build(getService());
    }


}
