package run.mone.m78.ip.client;

import okhttp3.OkHttpClient;

import java.util.concurrent.TimeUnit;

/**
 * @author caobaoyu
 * @description:
 * @date 2023-04-21 17:15
 */
public class MyHttpClient {

    private static OkHttpClient instance;

    private MyHttpClient() {
    }

    public static OkHttpClient getInstance() {
        if (instance == null) {
            instance = new OkHttpClient.Builder()
                    .connectTimeout(1, TimeUnit.SECONDS)
                    .readTimeout(10, TimeUnit.SECONDS)
                    .writeTimeout(1, TimeUnit.SECONDS)
                    .build();
        }
        return instance;
    }

    public static void close() {
        instance = getInstance();
        if (instance == null) {
            return;
        }
        instance.dispatcher().executorService().shutdown();
        instance.connectionPool().evictAll();
    }

}
