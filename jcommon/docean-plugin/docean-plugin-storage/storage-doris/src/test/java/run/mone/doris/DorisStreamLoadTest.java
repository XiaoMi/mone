package run.mone.doris;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author wtt
 * @version 1.0
 * @description
 * @date 2024/1/10 10:53
 */
@Slf4j
public class DorisStreamLoadTest {

    private DorisStreamLoad dorisStreamLoad;

    private String tableName = "doris_test_db";

    private Gson gson = new Gson();

    private List<String> columnList = Lists.newArrayList("id", "name", "message");
    private List<String> columnListNew = Lists.newArrayList("timestamp", "level", "traceId",
            "threadName", "className", "line",
            "methodName", "message", "podName",
            "logstore", "logsource", "mqtopic",
            "mqtag", "logip", "tail", "linenumber", "tailId");

    @Before
    public void init() {
        String host = "127.0.0.1";
        int port = 8030;
        String user = "root";
        String password = "";
        dorisStreamLoad = new DorisStreamLoad(host, user, password, port);
    }

    @Test
    public void sendDataTest() throws Exception {
        String database = "test";
        String tableName = "doris_test_db";
        List<List<String>> rows = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            List<String> columns = new ArrayList<>();
            columns.add(String.valueOf(i));
            columns.add("张三");
            columns.add("sfdsfdsfzhangsan发生士大夫但是发删掉发山东发sgdgdgdg");
            rows.add(columns);
        }
        dorisStreamLoad.sendData(database, tableName, rows);
    }

    @Test
    public void sendDataMapTest() throws Exception {
        String database = "test";
        String tableName = "hera_log_doris_table_2_11";
        for (int i = 0; i < 1; i++) {
            String jsonString = "{\"linenumber\":1256,\"tailId\":90219,\"mqtag\":\"tags_4_13_90219\",\"filename\":\"/home/work/log/log-agent/server.log\",\"tail\":\"demo-client-agent\",\"mqtopic\":\"90219_hera-demo-client\",\"message\":\"2024-01-10 19:34:40,360|INFO ||NettyClientPublicExecutor_1|c.x.mone.log.agent.rpc.task.PingTask|83|ping res: log-agent-server:2022-12-05:0.0.2->2024-01-10 19:34:40 358->10.53.129.250:9899\",\"logstore\":\"测试doris日志\",\"logip\":\"10.53.129.176\",\"timestamp\":1704886481241}";

            Map<String, Object> map = gson.fromJson(jsonString, Map.class);
            dorisStreamLoad.sendData(database, tableName, columnListNew, map);
        }
    }

    @Test
    public void sendDatasMapTest() throws Exception {
        String database = "test";
        String tableName = "doris_test_db";
        List<Map<String, Object>> dataList = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Map<String, Object> data = new HashMap<>();
            data.put("id", i);
            data.put("name", "张三");
            data.put("message", "股东风波的法规的规定翻跟斗广泛的给yryrtytr");
            System.out.println(gson.toJson(data));
            dataList.add(data);

        }
        dorisStreamLoad.sendData(database, tableName, columnList, dataList);
    }

    @Test
    public void sendJson() throws Exception {
        String database = "test";
        String tableName = "doris_test_db";
        List<String> dataList = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            Map<String, Object> data = new HashMap<>();
            data.put("id", i);
            data.put("name", "张三");
            data.put("message", "股东风波的法规的规定翻跟斗广泛的给yryrtytr");
//            dataList.add(data);

        }
        dataList.add("1");
        dataList.add("2");
        dataList.add("3");
        dorisStreamLoad.sendData(database, tableName, gson.toJson(dataList), false);
    }
}
