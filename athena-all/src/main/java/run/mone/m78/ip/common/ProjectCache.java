package run.mone.m78.ip.common;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;

import java.util.concurrent.ConcurrentHashMap;

public class ProjectCache {

    public static ConcurrentHashMap<String, Key> keyMap = new ConcurrentHashMap<>();

    public static void put(Project project, String key, Object value) {
        project.putUserData(getUserDataKey(key), value);
    }

    public static Object get(Project project, String key) {
        return project.getUserData(getUserDataKey(key));
    }

    public static void invalidate(Project project, String key) {
        project.putUserData(getUserDataKey(key), null);
    }

    private static Key getUserDataKey(String keyName) {
        return keyMap.compute(keyName, (k, v) -> {
            if (null == v) {
                return new Key(k);
            }
            return v;
        });
    }
}
