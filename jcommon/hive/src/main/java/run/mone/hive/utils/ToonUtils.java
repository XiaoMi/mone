package run.mone.hive.utils;

import dev.toonformat.jtoon.DecodeOptions;
import dev.toonformat.jtoon.Delimiter;
import dev.toonformat.jtoon.JToon;
import dev.toonformat.jtoon.PathExpansion;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * TOON 格式处理工具类
 * 提供 TOON 编解码、JSON 转换以及文件读写功能
 *
 * TOON 是一种轻量级的数据序列化格式，相比 JSON 更加紧凑，适合 LLM 场景
 */
@Slf4j
public class ToonUtils {

    // ==================== 基本编解码方法 ====================

    /**
     * 将 Java 对象编码为 TOON 字符串
     *
     * @param data 要编码的对象
     * @return TOON 格式字符串
     */
    public static String encode(Object data) {
        try {
            return JToon.encode(data);
        } catch (Exception e) {
            log.error("Failed to encode object to TOON: {}", e.getMessage());
            throw new RuntimeException("Failed to encode object to TOON", e);
        }
    }

    /**
     * 将 TOON 字符串解码为 Java 对象
     *
     * @param toon TOON 格式字符串
     * @return 解码后的对象
     */
    public static Object decode(String toon) {
        try {
            return JToon.decode(toon);
        } catch (Exception e) {
            log.error("Failed to decode TOON string: {}", e.getMessage());
            throw new RuntimeException("Failed to decode TOON string", e);
        }
    }

    /**
     * 将 TOON 字符串解码为 Java 对象（宽松模式，错误时返回 null）
     *
     * @param toon TOON 格式字符串
     * @return 解码后的对象，失败时返回 null
     */
    public static Object decodeLenient(String toon) {
        try {
            DecodeOptions options = DecodeOptions.withStrict(false);
            return JToon.decode(toon, options);
        } catch (Exception e) {
            log.warn("Failed to decode TOON string in lenient mode: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 将 TOON 字符串解码为指定类型的对象
     *
     * @param toon TOON 格式字符串
     * @param options 解码选项
     * @return 解码后的对象
     */
    public static Object decode(String toon, DecodeOptions options) {
        try {
            return JToon.decode(toon, options);
        } catch (Exception e) {
            log.error("Failed to decode TOON string with options: {}", e.getMessage());
            throw new RuntimeException("Failed to decode TOON string with options", e);
        }
    }

    /**
     * 使用 pipe 分隔符解码 TOON 字符串
     * 例如: "tags[3|]: a|b|c"
     *
     * @param toon TOON 格式字符串（使用 | 作为分隔符）
     * @return 解码后的对象
     */
    public static Object decodeWithPipe(String toon) {
        try {
            DecodeOptions options = new DecodeOptions(2, Delimiter.PIPE, true, PathExpansion.OFF);
            return JToon.decode(toon, options);
        } catch (Exception e) {
            log.error("Failed to decode TOON string with pipe delimiter: {}", e.getMessage());
            throw new RuntimeException("Failed to decode TOON string with pipe delimiter", e);
        }
    }

    // ==================== JSON 转换方法 ====================

    /**
     * 将 JSON 字符串转换为 TOON 格式
     *
     * @param json JSON 格式字符串
     * @return TOON 格式字符串
     */
    public static String encodeJson(String json) {
        try {
            return JToon.encodeJson(json);
        } catch (Exception e) {
            log.error("Failed to convert JSON to TOON: {}", e.getMessage());
            throw new RuntimeException("Failed to convert JSON to TOON", e);
        }
    }

    /**
     * 将 TOON 字符串转换为 JSON 格式
     *
     * @param toon TOON 格式字符串
     * @return JSON 格式字符串
     */
    public static String decodeToJson(String toon) {
        try {
            return JToon.decodeToJson(toon);
        } catch (Exception e) {
            log.error("Failed to convert TOON to JSON: {}", e.getMessage());
            throw new RuntimeException("Failed to convert TOON to JSON", e);
        }
    }

    /**
     * 将对象转换为 JSON 字符串（通过 TOON 中转）
     * 用于比较或调试
     *
     * @param data 要转换的对象
     * @return JSON 格式字符串
     */
    public static String toJson(Object data) {
        try {
            String toon = encode(data);
            return decodeToJson(toon);
        } catch (Exception e) {
            log.error("Failed to convert object to JSON via TOON: {}", e.getMessage());
            throw new RuntimeException("Failed to convert object to JSON via TOON", e);
        }
    }

    // ==================== 文件操作方法 ====================

    /**
     * 将对象编码为 TOON 格式并写入文件
     *
     * @param path 文件路径
     * @param data 要写入的对象
     */
    public static void writeToonFile(Path path, Object data) {
        try {
            String toon = encode(data);
            Files.writeString(path, toon);
        } catch (IOException e) {
            log.error("Error writing TOON file: {}", e.getMessage());
            throw new RuntimeException("Failed to write TOON file", e);
        }
    }

    /**
     * 从文件读取 TOON 格式并解码为对象
     *
     * @param path 文件路径
     * @return 解码后的对象
     */
    public static Object readToonFile(Path path) {
        try {
            String toon = Files.readString(path);
            return decode(toon);
        } catch (IOException e) {
            log.error("Error reading TOON file: {}", e.getMessage());
            throw new RuntimeException("Failed to read TOON file", e);
        }
    }

    /**
     * 从文件读取 TOON 格式并转换为 JSON 字符串
     *
     * @param path 文件路径
     * @return JSON 格式字符串
     */
    public static String readToonFileAsJson(Path path) {
        try {
            String toon = Files.readString(path);
            return decodeToJson(toon);
        } catch (IOException e) {
            log.error("Error reading TOON file as JSON: {}", e.getMessage());
            throw new RuntimeException("Failed to read TOON file as JSON", e);
        }
    }

    // ==================== 便捷方法 ====================

    /**
     * 判断字符串是否为有效的 TOON 格式
     *
     * @param toon 待验证的字符串
     * @return 是否为有效的 TOON 格式
     */
    public static boolean isValidToon(String toon) {
        try {
            JToon.decode(toon);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 将对象转换为紧凑的 TOON 格式字符串
     * TOON 本身就是紧凑格式，此方法主要用于确保没有多余空白
     *
     * @param data 要编码的对象
     * @return 紧凑的 TOON 格式字符串
     */
    public static String encodeCompact(Object data) {
        return encode(data).trim();
    }
}
