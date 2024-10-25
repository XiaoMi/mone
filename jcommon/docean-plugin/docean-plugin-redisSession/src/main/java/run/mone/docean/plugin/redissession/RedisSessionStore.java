package run.mone.docean.plugin.redissession;

import com.google.gson.Gson;
import com.xiaomi.youpin.docean.mvc.session.DefaultHttpSession;
import com.xiaomi.youpin.docean.mvc.session.HttpSession;
import com.xiaomi.youpin.docean.mvc.session.ISessionStore;
import com.xiaomi.youpin.docean.plugin.redis.Redis;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * @author shanwb
 * @date 2024-09-03
 */
@Slf4j
public class RedisSessionStore implements ISessionStore {

    private static final String SESSION_PREFIX = "DOCEAN_SESSION_";

    private static final int SESSION_EXPIRE_SECONDS = 3600; // 默认60分钟过期

    private final static Gson gson = new Gson();

    private Redis redis;

    public RedisSessionStore(Redis redis) {
        this.redis = redis;
    }

    @Override
    public void put(String sessionId, HttpSession session) {
        try {
            String key = SESSION_PREFIX + sessionId;
            String value = gson.toJson(session);
            redis.set(key, value, SESSION_EXPIRE_SECONDS);
        } catch (Exception e) {
            log.error("Error putting session to redis", e);
        }
    }

    @Override
    public void remove(String sessionId) {
        try {
            String key = SESSION_PREFIX + sessionId;
            redis.del(key);
        } catch (Exception e) {
            log.error("Error removing session from redis", e);
        }
    }

    @Override
    public List<String> sessionIdList() {
        return new ArrayList<>();
    }

    @Override
    public HttpSession get(String sessionId) {
        try {
            String key = SESSION_PREFIX + sessionId;
            String value = redis.get(key);
            if (value == null) {
                return null;
            }
            return gson.fromJson(value, DefaultHttpSession.class);
        } catch (Exception e) {
            log.error("Error getting session from redis", e);
            return null;
        }
    }

    @Override
    public HttpSession getAndRefresh(String sessionId) {
        try {
            String key = SESSION_PREFIX + sessionId;
            String value = redis.get(key);
            if (value == null) {
                return null;
            }
            DefaultHttpSession session = gson.fromJson(value, DefaultHttpSession.class);
            redis.expire(key, SESSION_EXPIRE_SECONDS);
            return session;
        } catch (Exception e) {
            log.error("Error getting session from redis", e);
            return null;
        }
    }

    @Override
    public boolean containsKey(String sessionId) {
        try {
            String key = SESSION_PREFIX + sessionId;
            return redis.exists(key);
        } catch (Exception e) {
            log.error("Error checking session existence in redis", e);
            return false;
        }
    }
}
