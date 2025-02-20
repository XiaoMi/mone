package run.mone.local.docean.service;
import com.knuddels.jtokkit.Encodings;
import com.knuddels.jtokkit.api.Encoding;
import com.knuddels.jtokkit.api.EncodingRegistry;
import com.knuddels.jtokkit.api.ModelType;
/**
 * @author zhangxiaowei6
 * @Date 2024/12/6 11:01
 */

public class SimpleTokenCounter {
    private static final EncodingRegistry registry = Encodings.newDefaultEncodingRegistry();
    private static final Encoding encoding = registry.getEncodingForModel(ModelType.GPT_4O);

    /**
     * 计算文本的 token 数量
     * @param text 需要计算的文本
     * @return token 数量
     */
    public static int calculateTokens(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }

        try {
            int tokens = encoding.countTokens(text);
            System.out.println("Text length: " + text.length() + " characters");
            System.out.println("Token count: " + tokens);
            return tokens;
        } catch (Exception e) {
            System.err.println("Error calculating tokens: " + e.getMessage());
            // 如果计算失败，返回一个保守的估计值（每4个字符约1个token）
            return (int) Math.ceil(text.length() / 4.0);
        }
    }

    public static int getImageTokens(String base64Image) {
        // base64 string length to tokens conversion
        // 每个 token 大约可以编码 3-4 个字符
        int base64Length = base64Image.length();
        int estimatedTokens = (int) Math.ceil(base64Length / 3.5);

        System.out.println("Base64 image length: " + base64Length + " characters");
        System.out.println("Estimated image tokens: " + estimatedTokens);

        return estimatedTokens;
    }
}
