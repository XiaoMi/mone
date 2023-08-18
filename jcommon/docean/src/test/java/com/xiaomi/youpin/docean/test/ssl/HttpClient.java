package com.xiaomi.youpin.docean.test.ssl;

import lombok.SneakyThrows;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

import javax.net.ssl.*;
import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

/**
 * @author goodjava@qq.com
 * @date 2023/8/17 14:19
 */
public class HttpClient {


    @SneakyThrows
    public static void call() {
        CertificateFactory cf = CertificateFactory.getInstance("X.509");

        InputStream caInput = new BufferedInputStream(new FileInputStream("/Users/zhangzhiyong/key/zzy.com/certificate.crt"));
        Certificate ca;
        try {
            ca = cf.generateCertificate(caInput);
            System.out.println("ca=" + ((X509Certificate) ca).getSubjectDN());
        } finally {
            caInput.close();
        }

        String keyStoreType = KeyStore.getDefaultType();
        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
        keyStore.load(null, null);
        keyStore.setCertificateEntry("ca", ca);

        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
        tmf.init(keyStore);

        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, tmf.getTrustManagers(), null);

        X509TrustManager trustManager = new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[]{}; // 返回受信任的证书数组
            }
            public void checkClientTrusted(X509Certificate[] chain, String authType) {
                // 检查客户端证书
            }
            public void checkServerTrusted(X509Certificate[] chain, String authType) {
                // 检查服务器证书
            }
        };

        OkHttpClient client = new OkHttpClient.Builder()
                .sslSocketFactory(sslContext.getSocketFactory(), trustManager)
                .hostnameVerifier((hostname, session) -> true)
                .build();

        Request request = new Request.Builder()
                .url("https://zzy.com:8999/a?id="+System.currentTimeMillis())
                .build();

        Response res = client.newCall(request).execute();
        ResponseBody body = res.body();
        String str = body.string();
        System.out.println(str);
    }

}
