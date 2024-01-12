package common;

import java.util.Map;

public class HttpResult {
        public final int code;
        public final String content;
        public byte[] data;
        private final Map<String, String> respHeaders;

        public HttpResult(int code, String content, Map<String, String> respHeaders) {
            this.code = code;
            this.content = content;
            this.respHeaders = respHeaders;
        }

        public String getHeader(String name) {
            return this.respHeaders.get(name);
        }

        public Map<String, String> getHeaders() {
            return this.respHeaders;
        }

    }