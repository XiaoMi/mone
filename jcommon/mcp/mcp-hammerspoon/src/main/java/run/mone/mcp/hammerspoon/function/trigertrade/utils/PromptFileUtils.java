package run.mone.mcp.hammerspoon.function.trigertrade.utils;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

/**
 * @author shanwb
 * @date 2025-03-12
 */
public class PromptFileUtils {

    // 股票行情
    public static String STOCK_QUOTE_PROMPT = "stockquote.prompt";

    //期权链
    public static String OPTION_CHAIN_PROMPT = "optionchain.prompt";

    //put 期权选择策略
    public static String SELL_PUT_STRATEGY_PROMPT = "sellPutStrategy.prompt";

    /**
     * 读取prompt目录下的文件内容
     *
     * @param fileName prompt目录下的文件名，例如："stockquote.prompt"
     * @return 文件内容字符串
     * @throws IOException 如果文件不存在或读取失败
     */
    public static String readPromptFile(String fileName) throws IOException {
        return readResourceFile("prompt/" + fileName);
    }

//    public static String templateReplace(String template, Object pojo) {
//        // 将POJO转换为Map
//        Map<String, Object> valueMap = new HashMap<>();
//        BeanMap beanMap = BeanMap.create(pojo);
//        for (Object key : beanMap.keySet()) {
//            valueMap.put(key.toString(), beanMap.get(key));
//        }
//
//        // 替换模板中的变量
//        StringSubstitutor substitutor = new StringSubstitutor(valueMap);
//        return substitutor.replace(template);
//    }



    /**
     * 读取resources目录下的文件内容
     *
     * @param resourcePath resources目录下的相对路径，例如："prompt/stockquote.prompt"
     * @return 文件内容字符串
     * @throws IOException 如果文件不存在或读取失败
     */
    public static String readResourceFile(String resourcePath) throws IOException {
        ClassLoader classLoader = PromptFileUtils.class.getClassLoader();
        try (InputStream inputStream = classLoader.getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                throw new IOException("Resource not found: " + resourcePath);
            }

            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                return reader.lines().collect(Collectors.joining(System.lineSeparator()));
            }
        }
    }

    /**
     * 判断prompt文件是否存在
     *
     * @param fileName prompt目录下的文件名
     * @return 文件是否存在
     */
    public static boolean promptFileExists(String fileName) {
        ClassLoader classLoader = PromptFileUtils.class.getClassLoader();
        return classLoader.getResource("prompt/" + fileName) != null;
    }
}