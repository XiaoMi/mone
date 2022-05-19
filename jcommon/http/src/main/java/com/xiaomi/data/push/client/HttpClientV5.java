package com.xiaomi.data.push.client;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.io.ByteStreams;
import com.google.common.net.HttpHeaders;
import com.xiaomi.youpin.cat.CatPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.zip.GZIPInputStream;


/**
 * @author goodjava@qq.com
 */
public class HttpClientV5 {

    private static final Logger logger = LoggerFactory.getLogger(HttpClientV5.class);

    private static boolean catEnabled;

    public static void enableCat() {
        catEnabled = true;
    }

    public static void disableCat() {
        catEnabled = false;
    }

    private static final String CAT_TYPE = "httpClient";

    public static String get(String url, Map<String, String> headers) {
        return get(url, headers, 500);
    }

    public static String get(String url, Map<String, String> headers, int readTimeout) {
        List<String> list = Lists.newArrayList();
        if (null != headers) {
            headers.entrySet().stream().forEach(it -> {
                list.add(it.getKey());
                list.add(it.getValue());
            });
        }
        return httpGet(url, list, Maps.newHashMap(), "UTF-8", readTimeout).content;
    }


    public static byte[] download(String url, int timeout, File file) {
        List<String> list = Lists.newArrayList();
        Map<String, String> map = Maps.newHashMap();
        HttpResult res = httpGet(url, list, map, "UTF-8", timeout, true, file);
        if (res.code != 200) {
            logger.warn("download:{} error:{}", url, res.code);
            return new byte[]{};
        }
        return res.data;
    }


    public static byte[] download(String url, int timeout) {
        return download(url, timeout, null);
    }

    public static String upload(String url, byte[] data) {
        String res = post(url, data, Maps.newHashMap(), 1000);
        logger.info("upload res:{}", res);
        return res;
    }

    public static HttpResult httpGet(String url, Map<String, String> headers) {
        List<String> list = Lists.newArrayList();
        if (null != headers) {
            headers.entrySet().stream().forEach(it -> {
                list.add(it.getKey());
                list.add(it.getValue());
            });
        }
        return httpGet(url, list, Maps.newHashMap(), "UTF-8");
    }


    public static String post(String url, String body, Map<String, String> headers, int timeout) {
        return post(url, body.getBytes(), headers, timeout);
    }


    public static String post(String url, byte[] body, Map<String, String> headers, int timeout) {
        CatPlugin cat = new CatPlugin("POST", catEnabled, CAT_TYPE);
        boolean success = true;
        cat.before(null);
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) new URL(url).openConnection();
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(timeout);
            conn.setReadTimeout(timeout);
            conn.setDoOutput(true);
            conn.setDoInput(true);

            if (null != headers) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    conn.addRequestProperty(entry.getKey(), entry.getValue());
                }
            }
            conn.addRequestProperty("Connection", "close");
            conn.getOutputStream().write(body);
            return new String(ByteStreams.toByteArray(conn.getInputStream()));
        } catch (Exception ex) {
            success = false;
            logger.warn("http client v2 error:{}", ex.getMessage());
            throw new RuntimeException(ex.getMessage());
        } finally {
            if (null != conn) {
                conn.disconnect();
            }
            cat.after(success);
        }

    }


    public static String post(String url, String body, Map<String, String> headers) {
        return post(url, body, headers, 500);
    }


    public static HttpResult httpGet(String url, List<String> headers, Map<String, String> paramValues, String encoding) {
        return request(url, headers, paramValues, encoding, "GET");
    }

    public static HttpResult httpGet(String url, List<String> headers, Map<String, String> paramValues, String encoding, int readTimeout) {
        return request(url, headers, paramValues, encoding, "GET", readTimeout, false, null);
    }

    public static HttpResult httpGet(String url, List<String> headers, Map<String, String> paramValues, String encoding, int readTimeout, boolean download, File file) {
        return request(url, headers, paramValues, encoding, "GET", readTimeout, download, file);
    }

    public static HttpResult request(String url, List<String> headers, Map<String, String> paramValues, String encoding, String method) {
        return request(url, headers, paramValues, encoding, method, 500, false, null);
    }

    public static HttpResult request(String url, List<String> headers, Map<String, String> paramValues, String encoding, String method, boolean download, File file) {
        return request(url, headers, paramValues, encoding, method, 500, download, file);
    }

    static TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
    }};

    public static class NullHostNameVerifier implements HostnameVerifier {
        @Override
        public boolean verify(String arg0, SSLSession arg1) {
            return true;
        }
    }


    public static HttpResult request(String url, List<String> headers, Map<String, String> paramValues, String encoding, String method, int readTimeout, boolean download, File file) {
        CatPlugin cat = new CatPlugin(method, catEnabled, CAT_TYPE);
        boolean success = true;
        cat.before(null);

        HttpURLConnection conn = null;
        try {
            String encodedContent = encodingParams(paramValues, encoding);
            url += (null == encodedContent) ? "" : ("?" + encodedContent);

            if (url.startsWith("https")) {
                HttpsURLConnection.setDefaultHostnameVerifier(new NullHostNameVerifier());
                SSLContext sc = SSLContext.getInstance("TLS");
                sc.init(null, trustAllCerts, new SecureRandom());
                HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
                conn = (HttpURLConnection) new URL(url).openConnection();
            } else {
                conn = (HttpURLConnection) new URL(url).openConnection();
            }


            conn.setConnectTimeout(readTimeout);
            conn.setReadTimeout(readTimeout);
            conn.setRequestMethod(method);
            setHeaders(conn, headers, encoding);
            conn.addRequestProperty("Connection", download ? "keep-alive" : "close");
            conn.connect();
            logger.debug("Request from server: " + url);
            return getResult(conn, download, file);
        } catch (Exception e) {
            try {
                if (conn != null) {
                    logger.warn("failed to request " + conn.getURL() + " from "
                        + InetAddress.getByName(conn.getURL().getHost()).getHostAddress());
                }
            } catch (Exception e1) {
                logger.warn("NA", "failed to request ", e1);
                //ignore
            }

            logger.warn("NA", "failed to request ", e);
            success = false;
            return new HttpResult(500, e.toString(), Collections.<String, String>emptyMap());
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
            cat.after(success);
        }
    }

    private static HttpResult getResult(HttpURLConnection conn, boolean download, File file) throws IOException {
        int respCode = conn.getResponseCode();

        InputStream inputStream;
        if (HttpURLConnection.HTTP_OK == respCode
            || HttpURLConnection.HTTP_NOT_MODIFIED == respCode) {
            inputStream = conn.getInputStream();
        } else {
            inputStream = conn.getErrorStream();
        }

        Map<String, String> respHeaders = new HashMap<String, String>(conn.getHeaderFields().size());
        for (Map.Entry<String, List<String>> entry : conn.getHeaderFields().entrySet()) {
            respHeaders.put(entry.getKey(), entry.getValue().get(0));
        }

        String encodingGzip = "gzip";

        if (encodingGzip.equals(respHeaders.get(HttpHeaders.CONTENT_ENCODING))) {
            inputStream = new GZIPInputStream(inputStream);
        }

        /**
         * 直接写到文件
         */
        if (null != file) {
            boolean res = getBytes(inputStream, file);
            return new HttpResult(respCode, res ? "57" : "", respHeaders);
        }


        byte[] data = download ? getBytes(inputStream) : ByteStreams.toByteArray(inputStream);
        HttpResult res = new HttpResult(respCode, new String(data, getCharset(conn)), respHeaders);
        res.data = data;
        return res;
    }


    public static byte[] getBytes(InputStream is) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        int nRead;
        byte[] data = new byte[1024 * 8];

        while ((nRead = is.read(data)) != -1) {
            if (Thread.currentThread().isInterrupted()) {
                logger.warn("interrupted");
                return new byte[]{};
            }
            buffer.write(data, 0, nRead);
        }

        byte[] res = buffer.toByteArray();
        is.close();
        buffer.close();


        return res;
    }

    /**
     * 直接下载到文件
     *
     * @param is
     * @param file
     * @throws IOException
     */
    public static boolean getBytes(InputStream is, File file) throws IOException {
        FileOutputStream fos = new FileOutputStream(file);
        int len;
        byte[] data = new byte[1024 * 8];

        try {
            while ((len = is.read(data)) != -1) {
                if (Thread.currentThread().isInterrupted()) {
                    logger.warn("interrupted download:{}", file);
                    return false;
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
        if (null == contentType || contentType.equals("")) {
            return "UTF-8";
        }

        String[] values = contentType.split(";");
        if (values.length == 0) {
            return "UTF-8";
        }

        String charset = "UTF-8";
        for (String value : values) {
            value = value.trim();

            if (value.toLowerCase().startsWith("charset=")) {
                charset = value.substring("charset=".length());
            }
        }

        return charset;
    }

    private static void setHeaders(HttpURLConnection conn, List<String> headers, String encoding) {
        if (null != headers) {
            for (Iterator<String> iter = headers.iterator(); iter.hasNext(); ) {
                conn.addRequestProperty(iter.next(), iter.next());
            }
        }
        conn.addRequestProperty("Accept-Charset", encoding);
    }

    private static String encodingParams(Map<String, String> params, String encoding)
        throws UnsupportedEncodingException {
        StringBuilder sb = new StringBuilder();
        if (null == params || params.isEmpty()) {
            return null;
        }

        params.put("encoding", encoding);

        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (null == entry.getValue() || entry.getValue().equals("")) {
                continue;
            }

            sb.append(entry.getKey()).append("=");
            sb.append(URLEncoder.encode(entry.getValue(), encoding));
            sb.append("&");
        }

        return sb.toString();
    }

    public static class HttpResult {
        final public int code;
        final public String content;
        public byte[] data;
        final private Map<String, String> respHeaders;

        public HttpResult(int code, String content, Map<String, String> respHeaders) {
            this.code = code;
            this.content = content;
            this.respHeaders = respHeaders;
        }

        public String getHeader(String name) {
            return respHeaders.get(name);
        }
    }
}
