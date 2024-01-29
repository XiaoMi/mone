package run.mone.doris;

import com.google.common.io.ByteStreams;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author wtt
 * @version 1.0
 * @description 通过stream load的方式写入数据，推荐使用这种方式
 * @date 2024/1/10 10:38
 */
@Slf4j
public class DorisStreamLoad {
    private final String DORIS_HOST;
    private final String DORIS_USER;
    private final String DORIS_PASSWORD;
    private final int DORIS_HTTP_PORT;

    private static JsonParser jsonParser = new JsonParser();

    public DorisStreamLoad(String host, String user, String pwd, int port) {
        DORIS_HOST = host;
        DORIS_USER = user;
        DORIS_PASSWORD = pwd;
        DORIS_HTTP_PORT = port;
    }

    public void sendData(String database, String table, String content, boolean partitioned) throws Exception {
        final String loadUrl = String.format("http://%s:%s/api/%s/%s/_stream_load?strip_outer_array=true",
                DORIS_HOST,
                DORIS_HTTP_PORT,
                database,
                table);

        final HttpClientBuilder httpClientBuilder = HttpClients
                .custom()
                .setRedirectStrategy(new DefaultRedirectStrategy() {
                    @Override
                    protected boolean isRedirectable(String method) {
                        return true;
                    }
                });

        try (CloseableHttpClient client = httpClientBuilder.build()) {
            HttpPut put = new HttpPut(loadUrl);
            StringEntity entity = new StringEntity(content, "UTF-8");
            put.setHeader(HttpHeaders.EXPECT, "100-continue");
            put.setHeader(HttpHeaders.AUTHORIZATION, HttpUtil.basicAuthHeader(DORIS_USER, DORIS_PASSWORD));
            put.setHeader("max_filter_ratio", "0.1");
            if (partitioned) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
                put.setHeader("partitions", "p" + simpleDateFormat.format(new Date()));
            }
            // the label header is optional, not necessary
            // use label header can ensure at most once semantics
            put.setEntity(entity);
            try (CloseableHttpResponse response = client.execute(put)) {
                String contentStr = new String(ByteStreams.toByteArray(response.getEntity().getContent()));
                JsonObject jsonObject = jsonParser.parse(contentStr).getAsJsonObject();
                log.info("result:{}", contentStr);
                int statusCode = response.getStatusLine().getStatusCode();
                // statusCode 200 just indicates that doris be service is ok, not stream load
                // you should see the output content to find whether stream load is success
                if (statusCode != HttpStatus.SC_OK || (!jsonObject.get("Status").getAsString().equalsIgnoreCase("SUCCESS") &&
                        !jsonObject.get("Status").getAsString().equalsIgnoreCase("Publish Timeout"))) {
                    throw new IOException(
                            String.format("Stream load failed, statusCode=%s load result=%s content=%s", statusCode, jsonObject.toString(), content));
                }
            } catch (Exception e) {
                log.error("error", e);
            } finally {
                client.close();
            }
        }
    }


    public void sendData(String database, String tableName, List<List<String>> rows) throws Exception {
        if (rows.isEmpty()) {
            return;
        }

        StringBuilder rowsData = new StringBuilder();
        for (List<String> row : rows) {
            StringBuilder rowData = new StringBuilder();
            for (int i = 0; i < row.size(); ++i) {
                rowData.append(row.get(i));
                if (i < row.size() - 1) {
                    rowData.append("\t");
                }
            }
            rowData.append("\n");
            rowsData.append(rowData);
        }
        sendData(database, tableName, rowsData.toString(), false);
    }

    public void sendData(String database, String tableName, List<String> columnList, Map<String, Object> rows) throws Exception {
        if (rows.isEmpty()) {
            return;
        }
        StringBuilder rowData = new StringBuilder();
        for (int i = 0; i < columnList.size(); i++) {
            rowData.append(rows.get(columnList.get(i)));
            if (i < columnList.size() - 1) {
                rowData.append("\t");
            }
        }

        sendData(database, tableName, rowData.toString(), false);
    }

    public void sendData(String database, String tableName, List<String> columnList, List<Map<String, Object>> rows) throws Exception {
        if (rows.isEmpty()) {
            return;
        }
        StringBuilder rowsData = new StringBuilder();
        for (Map<String, Object> row : rows) {

            StringBuilder rowData = new StringBuilder();
            for (int i = 0; i < columnList.size(); i++) {
                rowData.append(row.get(columnList.get(i)));
                if (i < columnList.size() - 1) {
                    rowData.append("\t");
                }
            }

            rowData.append("\n");
            rowsData.append(rowData);
        }

        sendData(database, tableName, rowsData.toString(), false);
    }
}
