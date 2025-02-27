package run.mone.moner.server.mcp;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.concurrent.TimeUnit;

/**
 * @author goodjava@qq.com
 * @date 2025/2/12 17:07
 */
public class CacheService {

    public static final String tools_key = "tools_key";

    private static class LazyHolder {
        private static CacheService ins = new CacheService();
    }

    public static final CacheService ins() {
        return LazyHolder.ins;
    }


    private Cache<String, Object> cache = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(30, TimeUnit.MINUTES)
            .build();


    //帮我用guava 中的cache 创建一个方法,用来缓存Object key 用string thx(class)
    public void cacheObject(String key, Object value) {
        cache.put(key, value);
    }

    //添加一个按key获取的方法(class)
    public Object getObject(String key) {
        return cache.getIfPresent(key);
    }

    //按key驱逐(class)
    public void evictObject(String key) {
        cache.invalidate(key);
    }


}
