//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package common;

import com.google.common.collect.Lists;
import com.google.common.io.ByteStreams;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Slf4j
public class HttpClientV6 {
//    private static final Logger log = LoggerFactory.getLogger(HttpClientV6.class);

    private final Gson gson = Util.getGson();

    static TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    }};

    public HttpClientV6() {
    }

    public static HttpResult postRt(TpsRecord needRecordTps, String url, byte[] body, Map<String, String> headers, int timeout) {
        HttpURLConnection conn = null;

        HttpResult var14;
        try {
            conn = (HttpURLConnection) (new URL(url)).openConnection();
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(timeout);
            conn.setReadTimeout(timeout);
            conn.setDoOutput(true);
            conn.setDoInput(true);
            if (null != headers) {
                Iterator var7 = headers.entrySet().iterator();

                while (var7.hasNext()) {
                    Map.Entry<String, String> entry = (Map.Entry) var7.next();
                    conn.addRequestProperty((String) entry.getKey(), (String) entry.getValue());
                }
            }

            conn.addRequestProperty("Connection", "close");
            conn.getOutputStream().write(body);

            var14 = getResult(conn, false, null);
        } catch (Exception var12) {
            log.warn("http client v2 error:{}", var12.getCause());
            needRecordTps.setNeedRecordTps(false);
            var14 = new HttpResult(500, var12.getMessage(), Collections.emptyMap());
        } finally {
            if (null != conn) {
                conn.disconnect();
            }
        }

        return var14;
    }

    public static HttpResult httpPost(TpsRecord needRecordTps, String url, Map<String, String> headers, Map<String, String> body, String encoding, int readTimeout) {
        List<String> list = Lists.newArrayList();
        if (null != headers) {
            headers.entrySet().stream().forEach((it) -> {
                list.add(it.getKey());
                list.add(it.getValue());
            });
        }

        return request(needRecordTps, url, list, body, encoding, "POST", readTimeout, false, null);
    }

    public static HttpResult httpGet(TpsRecord needRecordTps, String url, List<String> headers, Map<String, String> paramValues, String encoding, int readTimeout) {
        return request(needRecordTps, url, headers, paramValues, encoding, "GET", readTimeout, false, (File) null);
    }

    public static HttpResult request(TpsRecord needRecordTps, String url, List<String> headers, Map<String, String> paramValues, String encoding, String method, int readTimeout, boolean download, File file) {
        HttpURLConnection conn = null;
        OutputStreamWriter wr =  null;
        HttpResult var12;
        try {
            String encodedContent = encodingParams(paramValues, encoding);

            if (method.equalsIgnoreCase(Const.HTTP_GET)){
                url = url + "?" + encodedContent;
            }
            if (url.startsWith("https")) {
                HttpsURLConnection.setDefaultHostnameVerifier(new NullHostNameVerifier());
                SSLContext sc = SSLContext.getInstance("TLS");
                sc.init(null, trustAllCerts, new SecureRandom());
                HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            }
            conn = (HttpURLConnection) (new URL(url)).openConnection();
            conn.setConnectTimeout(readTimeout);
            conn.setReadTimeout(readTimeout);
            conn.setRequestMethod(method);
            setHeaders(conn, headers, encoding);
            conn.addRequestProperty("Connection", download ? "keep-alive" : "close");

            // 向服务器发送请求
            if (!method.equalsIgnoreCase(Const.HTTP_GET)){
                conn.setDoOutput(true);
                wr = new OutputStreamWriter(conn.getOutputStream());
                wr.write(encodedContent);
                wr.flush();
            }
            conn.connect();
            log.debug("Request from server: " + url);
            var12 = getResult(conn, download, file);
            return var12;
        } catch (Exception var18) {
            try {
                if (conn != null) {
                    log.warn("failed to request " + conn.getURL() + " from " + InetAddress.getByName(conn.getURL().getHost()).getHostAddress());
                }
            } catch (Exception var17) {
                log.warn("failed to request:{}", var17.getMessage());
            }

            log.warn("failed to request:{}", var18.getMessage());
            needRecordTps.setNeedRecordTps(false);
            var12 = new HttpResult(500, var18.getMessage(), Collections.emptyMap());
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
            if (wr != null){
                try {
                    wr.close();
                } catch (IOException e) {
                    log.error("failed to request,failed to close wr");
                }
            }
        }
        return var12;
    }

    private static HttpResult getResult(HttpURLConnection conn, boolean download, File file) throws IOException {
        int respCode = conn.getResponseCode();
        Object inputStream;
        if (200 != respCode && 304 != respCode && 204 != respCode && 201 != respCode) {
            inputStream = conn.getErrorStream();
        } else {
            inputStream = conn.getInputStream();
        }

        Map<String, String> respHeaders = new HashMap(conn.getHeaderFields().size());
        Iterator var6 = conn.getHeaderFields().entrySet().iterator();

        while (var6.hasNext()) {
            Map.Entry<String, List<String>> entry = (Map.Entry) var6.next();
            respHeaders.put(entry.getKey(), (String) ((List) entry.getValue()).get(0));
        }

        String encodingGzip = "gzip";
        if (encodingGzip.equals(respHeaders.get("Content-Encoding"))) {
            inputStream = new GZIPInputStream((InputStream) inputStream);
        }

        if (null != file) {
            boolean res = getBytes((InputStream) inputStream, file);
            return new HttpResult(respCode, res ? "57" : "", respHeaders);
        } else {
            HttpResult res;
            if (inputStream != null) {
                byte[] data = download ? getBytes((InputStream) inputStream) : ByteStreams.toByteArray((InputStream) inputStream);
                res = new HttpResult(respCode, new String(data, getCharset(conn)), respHeaders);
                res.data = data;
            } else {
                res = new HttpResult(respCode, String.valueOf(respCode), respHeaders);
                res.data = String.valueOf(respCode).getBytes();
            }
            return res;
        }
    }

    public static byte[] getBytes(InputStream is) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[8192];

        int nRead;
        while ((nRead = is.read(data)) != -1) {
            if (Thread.currentThread().isInterrupted()) {
                log.warn("interrupted");
                return new byte[0];
            }

            buffer.write(data, 0, nRead);
        }

        byte[] res = buffer.toByteArray();
        is.close();
        buffer.close();
        return res;
    }

    public static boolean getBytes(InputStream is, File file) throws IOException {
        FileOutputStream fos = new FileOutputStream(file);
        byte[] data = new byte[8192];

        try {
            int len;
            while ((len = is.read(data)) != -1) {
                if (Thread.currentThread().isInterrupted()) {
                    log.warn("interrupted download:{}", file);
                    boolean var5 = false;
                    return var5;
                }

                fos.write(data, 0, len);
            }

            fos.flush();
        } finally {
            is.close();
            fos.close();
        }

        return true;
    }

    private static String getCharset(HttpURLConnection conn) {
        String contentType = conn.getContentType();
        if (null != contentType && !contentType.equals("")) {
            String[] values = contentType.split(";");
            if (values.length == 0) {
                return "UTF-8";
            } else {
                String charset = "UTF-8";
                String[] var4 = values;
                int var5 = values.length;

                for (int var6 = 0; var6 < var5; ++var6) {
                    String value = var4[var6];
                    value = value.trim();
                    if (value.toLowerCase().startsWith("charset=")) {
                        charset = value.substring("charset=".length());
                    }
                }

                return charset;
            }
        } else {
            return "UTF-8";
        }
    }

    private static void setHeaders(HttpURLConnection conn, List<String> headers, String encoding) {
        if (null != headers) {
            Iterator<String> iter = headers.iterator();

            while (iter.hasNext()) {
                conn.addRequestProperty((String) iter.next(), (String) iter.next());
            }
        }

        conn.addRequestProperty("Accept-Charset", encoding);
    }

    private static String encodingParams(Map<String, String> params, String encoding) throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        if (null != params && !params.isEmpty()) {
            params.put("encoding", encoding);

            for (Map.Entry<String, String> stringStringEntry : params.entrySet()) {
                if (null != stringStringEntry.getValue() && !stringStringEntry.getValue().equals("")) {
                    sb.append(stringStringEntry.getKey()).append("=");
                    if (!encoding.isEmpty()) {
                        sb.append(URLEncoder.encode(stringStringEntry.getValue(), encoding));
                    } else {
                        sb.append(stringStringEntry.getValue());
                    }
                    sb.append("&");
                }
            }

            return sb.toString();
        } else {
            return "";
        }
    }

    public static class NullHostNameVerifier implements HostnameVerifier {
        public NullHostNameVerifier() {
        }

        public boolean verify(String arg0, SSLSession arg1) {
            return true;
        }
    }
}
