package com.xiaomi.mone.monitor.service.alertmanager.client.signer;

import com.xiaomi.mone.monitor.service.alertmanager.client.Request;
import com.xiaomi.mone.monitor.service.alertmanager.client.model.SigningAlgorithm;
import com.xiaomi.mone.monitor.service.alertmanager.client.util.BinaryUtils;
import com.xiaomi.mone.monitor.service.alertmanager.client.util.HttpUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author gaoxihui
 * @date 2022/11/10 3:07 下午
 */
public class Signer {

    public static final SimpleDateFormat TIME_FORMATTER = new SimpleDateFormat("yyyyMMdd'T'HHmmss'Z'");

    public Signer() {
    }

    public void sign(Request request) {
        String singerDate = this.getHeader(request, "X-Sdk-Date");
        if (singerDate == null) {
            singerDate = TIME_FORMATTER.format(new Date());
            request.addHeader("X-Sdk-Date", singerDate);
        }

        this.addHostHeader(request);
        String contentSha256 = this.calculateContentHash(request);
        String[] signedHeaders = this.getSignedHeaders(request);
        String canonicalRequest = this.createCanonicalRequest(request, signedHeaders, contentSha256);
        String stringToSign = this.createStringToSign(canonicalRequest, singerDate);
        byte[] signingKey = this.deriveSigningKey(request.getSecrect());
        byte[] signature = this.computeSignature(stringToSign, signingKey);
        request.addHeader("Authorization", this.buildAuthorizationHeader(signedHeaders, signature, request.getKey()));
    }

    protected String getCanonicalizedResourcePath(String resourcePath) {
        if (resourcePath != null && !resourcePath.isEmpty()) {
            try {
                resourcePath = (new URI(resourcePath)).getPath();
            } catch (URISyntaxException var3) {
                return resourcePath;
            }

            String value = HttpUtils.urlEncode(resourcePath, true);
            if (!value.startsWith("/")) {
                value = "/".concat(value);
            }

            if (!value.endsWith("/")) {
                value = value.concat("/");
            }

            return value;
        } else {
            return "/";
        }
    }

    protected String getCanonicalizedQueryString(Map<String, List<String>> parameters) {
        SortedMap<String, List<String>> sorted = new TreeMap();
        Iterator parametersIter = parameters.entrySet().iterator();

        Iterator iterator;
        while(parametersIter.hasNext()) {
            Map.Entry<String, List<String>> entry = (Map.Entry)parametersIter.next();
            String encodedParamName = HttpUtils.urlEncode((String)entry.getKey(), false);
            List<String> paramValues = (List)entry.getValue();
            List<String> encodedValues = new ArrayList(paramValues.size());
            iterator = paramValues.iterator();

            while(iterator.hasNext()) {
                String value = (String)iterator.next();
                encodedValues.add(HttpUtils.urlEncode(value, false));
            }

            Collections.sort(encodedValues);
            sorted.put(encodedParamName, encodedValues);
        }

        StringBuilder result = new StringBuilder();
        Iterator sortedIterator = sorted.entrySet().iterator();

        while(sortedIterator.hasNext()) {
            Map.Entry<String, List<String>> entry = (Map.Entry)sortedIterator.next();

            String value;
            for(iterator = ((List)entry.getValue()).iterator(); iterator.hasNext(); result.append((String)entry.getKey()).append("=").append(value)) {
                value = (String)iterator.next();
                if (result.length() > 0) {
                    result.append("&");
                }
            }
        }

        return result.toString();
    }

    protected String createCanonicalRequest(Request request, String[] signedHeaders, String contentSha256) {
        StringBuilder canonicalRequestBuilder = new StringBuilder(request.getMethod().toString());
        canonicalRequestBuilder.append("\n").append(this.getCanonicalizedResourcePath(request.getPath())).append("\n").append(this.getCanonicalizedQueryString(request.getQueryStringParams())).append("\n").append(this.getCanonicalizedHeaderString(request, signedHeaders)).append("\n").append(this.getSignedHeadersString(signedHeaders)).append("\n").append(contentSha256);
        String canonicalRequest = canonicalRequestBuilder.toString();
        System.out.println("canonicalRequest:" + canonicalRequest);
        return canonicalRequest;
    }

    protected String createStringToSign(String canonicalRequest, String singerDate) {
        StringBuilder stringToSignBuilder = new StringBuilder("SDK-HMAC-SHA256");
        stringToSignBuilder.append("\n").append(singerDate).append("\n").append(BinaryUtils.toHex(this.hash(canonicalRequest)));
        String stringToSign = stringToSignBuilder.toString();
        return stringToSign;
    }

    private final byte[] deriveSigningKey(String secret) {
        return secret.getBytes(StandardCharsets.UTF_8);
    }

    protected byte[] sign(byte[] data, byte[] key, SigningAlgorithm algorithm) {
        try {
            Mac mac = Mac.getInstance(algorithm.toString());
            mac.init(new SecretKeySpec(key, algorithm.toString()));
            return mac.doFinal(data);
        } catch (NoSuchAlgorithmException | InvalidKeyException var5) {
            return null;
        }
    }

    protected final byte[] computeSignature(String stringToSign, byte[] signingKey) {
        return this.sign(stringToSign.getBytes(StandardCharsets.UTF_8), signingKey, SigningAlgorithm.HmacSHA256);
    }

    private String buildAuthorizationHeader(String[] signedHeaders, byte[] signature, String accessKey) {
        String credential = "Access=" + accessKey;
        String signerHeaders = "SignedHeaders=" + this.getSignedHeadersString(signedHeaders);
        String signatureHeader = "Signature=" + BinaryUtils.toHex(signature);
        StringBuilder authHeaderBuilder = new StringBuilder();
        authHeaderBuilder.append("cloud-auth/1.0").append(" ").append("SDK-HMAC-SHA256").append(" ").append(credential).append(", ").append(signerHeaders).append(", ").append(signatureHeader);
        System.out.println("authheader:" + authHeaderBuilder.toString());
        return authHeaderBuilder.toString();
    }

    protected String[] getSignedHeaders(Request request) {
        String[] signedHeaders = (String[])((String[])request.getHeaders().keySet().toArray(new String[0]));
        Arrays.sort(signedHeaders, String.CASE_INSENSITIVE_ORDER);
        return signedHeaders;
    }

    protected String getCanonicalizedHeaderString(Request request, String[] signedHeaders) {
        Map<String, String> requestHeaders = request.getHeaders();
        StringBuilder buffer = new StringBuilder();
        String[] headersArr = signedHeaders;
        int headersLen = signedHeaders.length;

        for(int i = 0; i < headersLen; ++i) {
            String header = headersArr[i];
            String key = header.toLowerCase();
            String value = (String)requestHeaders.get(header);
            buffer.append(key).append(":");
            if (value != null) {
                buffer.append(value.trim());
            }

            buffer.append("\n");
        }

        return buffer.toString();
    }

    protected String getSignedHeadersString(String[] signedHeaders) {
        StringBuilder buffer = new StringBuilder();
        String[] headersArr = signedHeaders;
        int headersLen = signedHeaders.length;

        for(int i = 0; i < headersLen; ++i) {
            String header = headersArr[i];
            if (buffer.length() > 0) {
                buffer.append(";");
            }

            buffer.append(header.toLowerCase());
        }

        return buffer.toString();
    }

    protected void addHostHeader(Request request) {
        boolean haveHostHeader = false;
        Iterator headersIter = request.getHeaders().keySet().iterator();

        while(headersIter.hasNext()) {
            String key = (String)headersIter.next();
            if ("Host".equalsIgnoreCase(key)) {
                haveHostHeader = true;
                break;
            }
        }

        if (!haveHostHeader) {
            request.addHeader("Host", request.getHost());
        }

    }

    protected String getHeader(Request request, String header) {
        if (header == null) {
            return null;
        } else {
            Map<String, String> headers = request.getHeaders();
            Iterator headersIter = headers.keySet().iterator();

            while(headersIter.hasNext()) {
                String key = (String)headersIter.next();
                if (header.equalsIgnoreCase(key)) {
                    return (String)headers.get(key);
                }
            }

            return null;
        }
    }


    protected String calculateContentHash(Request request) {
        String content_sha256 = this.getHeader(request, "x-sdk-content-sha256");
        return content_sha256 != null ? content_sha256 : BinaryUtils.toHex(this.hash(request.getBody()));
    }

    public byte[] hash(String text) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(text.getBytes(StandardCharsets.UTF_8));
            return md.digest();
        } catch (NoSuchAlgorithmException var3) {
            return null;
        }
    }


    static {
        TIME_FORMATTER.setTimeZone(TimeZone.getTimeZone("UTC"));
    }
}
