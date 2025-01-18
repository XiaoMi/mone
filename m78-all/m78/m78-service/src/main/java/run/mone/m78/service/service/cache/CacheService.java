package run.mone.m78.service.service.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

/**
 * @author goodjava@qq.com
 * @date 2024/1/24 14:02
 */
@Service
public class CacheService {


    private Cache<String, Object> cache;

    /**
     * 初始化缓存配置
     *
     * 使用CacheBuilder创建一个缓存实例，缓存项在写入10分钟后过期，最多缓存5000个项。
     */
	@PostConstruct
    public void init() {
        this.cache = CacheBuilder.newBuilder()
                .expireAfterWrite(10, TimeUnit.MINUTES) // 缓存项在写入10分钟后过期
                .maximumSize(5000) // 最多缓存1000个项
                .build();
    }


    /**
     * 根据键从缓存中获取值
     *
     * @param <T> 返回值的类型
     * @param key 缓存的键
     * @return 缓存中对应键的值，如果不存在则返回null
     */
	public <T> T get(String key) {
        return (T) cache.getIfPresent(key);
    }

    /**
     * 将指定的键值对存入缓存
     *
     * @param key 键
     * @param value 值
     */
	public void set(String key, Object value) {
        cache.put(key, value);
    }




}
