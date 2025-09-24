package run.mone.local.docean.util;

import com.xiaomi.data.push.client.HttpClientV2;
import com.xiaomi.data.push.client.Pair;

import java.util.Base64;

/**
 * @author wmin
 * @date 2024/7/22
 */
public class ImageUtils {

    public static ImageData downloadImageAsBase64(String url) {
        byte[] fileBytes = HttpClientV2.download(url, 3000);
        if (fileBytes == null) {
            return null;
        }

        String base64String = Base64.getEncoder().encodeToString(fileBytes);
        Pair<String, String> pair = getImageTypeAndPrefixByUrl(url);
        return new ImageData(base64String, pair.getKey(), pair.getValue());
    }

    // 用于存储Base64字符串和图片类型的类
    public static class ImageData {
        private String base64String;
        private String imageType;//JPEG、PNG、GIF 和 WebP
        private String imagePrefix;//image/jpeg、image/png、image/gif 和 image/webp

        public ImageData(String base64String, String imageType, String imagePrefix) {
            this.base64String = base64String;
            this.imageType = imageType;
            this.imagePrefix = imagePrefix;
        }

        public String getBase64String() {
            return base64String;
        }

        public String getImageType() {
            return imageType;
        }

        public String getImagePrefix() {
            return imagePrefix;
        }
    }

    //根据url中图片后缀返回imageType和imagePrefix，url可能是https://XXX/m78-inner/avatar-tSTsv05oZk-1723114709299.png，或者https://XXX/m78-sder/avatar-tSTsv05oZk-1723114709299.png?XXX
    public static Pair<String, String> getImageTypeAndPrefixByUrl(String url) {
        String imageType = null;
        String imagePrefix = null;

        // 提取图片后缀
        String[] urlParts = url.split("\\?");
        String path = urlParts[0];
        String extension = path.substring(path.lastIndexOf('.') + 1).toLowerCase();

        switch (extension) {
            case "jpeg":
            case "jpg":
                imageType = "jpeg";
                imagePrefix = "image/jpeg";
                break;
            case "png":
                imageType = "png";
                imagePrefix = "image/png";
                break;
            case "gif":
                imageType = "gif";
                imagePrefix = "image/gif";
                break;
            case "webp":
                imageType = "webp";
                imagePrefix = "image/webp";
                break;
            default:
                throw new RuntimeException("unknown image type ");
        }
        return Pair.of(imageType, imagePrefix);
    }


    public static void main(String[] args) {
        ImageData imageData = downloadImageAsBase64("https://X.net/m78-inner/avatar-tSTsv05oZk-1723114709299.png");
        System.out.println(imageData.imageType);
        System.out.println(imageData.imagePrefix);
    }
}
