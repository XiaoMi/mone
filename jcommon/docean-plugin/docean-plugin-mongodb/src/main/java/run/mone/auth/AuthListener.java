package run.mone.auth;

import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.listener.Listener;
import com.xiaomi.youpin.docean.listener.event.Event;
import com.xiaomi.youpin.docean.listener.event.EventType;
import com.xiaomi.youpin.docean.mvc.HttpRequestMethod;
import dev.morphia.Datastore;
import dev.morphia.query.filters.Filters;
import lombok.extern.slf4j.Slf4j;
import run.mone.bo.PathAuth;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author goodjava@qq.com
 * @date 2024/4/23 09:30
 */
@Slf4j
public class AuthListener implements Listener {

    ConcurrentMap<String, PathAuth> map = new ConcurrentHashMap<>();

    public AuthListener(ConcurrentMap<String, PathAuth> map) {
        this.map = map;
    }

    /**
     * 重写onEvent方法,用于处理事件
     * 如果事件类型为initControllerFinish,则获取事件数据中的请求方法映射
     * 遍历请求方法映射,获取每个请求方法的路径和注解信息
     * 根据注解信息确定该路径所需的角色权限
     * 如果数据库中不存在该路径的权限记录,则创建一条新记录并插入数据库
     * 将路径和权限记录存入map中
     */
    @Override
    public void onEvent(Event event) {
        if (event.getEventType().equals(EventType.initControllerFinish)) {
            ConcurrentHashMap<String, HttpRequestMethod> requestMethodMap = event.getData();
            log.info("map size:{}", requestMethodMap.size());
            Datastore datastore = Ioc.ins().getBean(Datastore.class);
            requestMethodMap.values().forEach(it -> {
                try {
                    String path = it.getPath();
                    Method method = it.getMethod();
                    Auth auth = method.getAnnotation(Auth.class);
                    String role = "user";
                    PathAuth pa = datastore.find(PathAuth.class).filter(Filters.eq("path", path)).first();
                    if (null == pa) {
                        if (null != auth) {
                            role = auth.role();
                        }
                        pa = PathAuth.builder().path(path).role(role).build();
                        datastore.insert(pa);
                    }
                    map.put(path, pa);
                } catch (Throwable ex) {
                    ex.printStackTrace();
                }
            });
        }
    }
}
