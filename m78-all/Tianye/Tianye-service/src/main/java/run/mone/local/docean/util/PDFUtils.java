package run.mone.local.docean.util;

import com.xiaomi.data.push.client.HttpClientV2;
import com.xiaomi.data.push.client.Pair;

import java.util.Base64;

public class PDFUtils {
    // 用于存储PDF的类
    public static class PDFData {
        private byte[] data;

        public PDFData(byte[] base64String) {
            this.data = base64String;
        }

        public byte[] getBase64String() {
            return data;
        }
    }

    // 下载pdf文件
    public static PDFUtils.PDFData downloadPDFAsBase64(String url) {
        byte[] fileBytes = HttpClientV2.download(url, 3000);
        if (fileBytes == null) {
            return null;
        }
        return new PDFUtils.PDFData(fileBytes);
    }

}
