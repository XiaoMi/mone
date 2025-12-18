package run.mone.hive.spring.starter;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 屏幕尺寸缓存
 * 用于缓存每个 Android 设备 (clientId) 对应的屏幕尺寸
 *
 * @author shanwb
 * @date 2025/12/17
 */
@Slf4j
public class ScreenSizeCache {

    private static final ScreenSizeCache INSTANCE = new ScreenSizeCache();

    // 默认屏幕尺寸（当缓存中没有对应设备时使用）
    private static final int DEFAULT_WIDTH = 1080;
    private static final int DEFAULT_HEIGHT = 2400;

    // clientId -> ScreenSize 的映射
    private final ConcurrentHashMap<String, ScreenSize> cache = new ConcurrentHashMap<>();

    private ScreenSizeCache() {
    }

    public static ScreenSizeCache getInstance() {
        return INSTANCE;
    }

    /**
     * 更新设备的屏幕尺寸
     *
     * @param clientId     设备 clientId
     * @param screenWidth  屏幕宽度
     * @param screenHeight 屏幕高度
     */
    public void updateScreenSize(String clientId, int screenWidth, int screenHeight) {
        if (clientId == null || clientId.isEmpty()) {
            log.warn("updateScreenSize: clientId is null or empty");
            return;
        }
        if (screenWidth <= 0 || screenHeight <= 0) {
            log.warn("updateScreenSize: invalid screen size {}x{} for clientId={}", screenWidth, screenHeight, clientId);
            return;
        }
        ScreenSize size = new ScreenSize(screenWidth, screenHeight);
        cache.put(clientId, size);
        log.info("Updated screen size for clientId={}: {}x{}", clientId, screenWidth, screenHeight);
    }

    /**
     * 获取设备的屏幕尺寸
     *
     * @param clientId 设备 clientId
     * @return 屏幕尺寸，如果不存在则返回默认值
     */
    public ScreenSize getScreenSize(String clientId) {
        if (clientId == null || clientId.isEmpty()) {
            log.warn("getScreenSize: clientId is null or empty, using default size");
            return new ScreenSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        }
        ScreenSize size = cache.get(clientId);
        if (size == null) {
            log.warn("getScreenSize: no cached size for clientId={}, using default size {}x{}",
                    clientId, DEFAULT_WIDTH, DEFAULT_HEIGHT);
            return new ScreenSize(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        }
        return size;
    }

    /**
     * 检查是否有缓存的屏幕尺寸
     *
     * @param clientId 设备 clientId
     * @return true 如果存在缓存
     */
    public boolean hasScreenSize(String clientId) {
        return clientId != null && cache.containsKey(clientId);
    }

    /**
     * 移除设备的屏幕尺寸缓存
     *
     * @param clientId 设备 clientId
     */
    public void removeScreenSize(String clientId) {
        if (clientId != null) {
            cache.remove(clientId);
            log.info("Removed screen size cache for clientId={}", clientId);
        }
    }

    /**
     * 清空所有缓存
     */
    public void clear() {
        cache.clear();
        log.info("Cleared all screen size cache");
    }

    /**
     * 屏幕尺寸数据类
     */
    @Data
    public static class ScreenSize {
        private final int width;
        private final int height;

        public ScreenSize(int width, int height) {
            this.width = width;
            this.height = height;
        }
    }
}
