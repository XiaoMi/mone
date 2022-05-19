package com.xiaomi.data.push.action;

import com.google.gson.GsonBuilder;
import com.xiaomi.data.push.annotation.Action;
import com.xiaomi.data.push.annotation.Cache;
import com.xiaomi.data.push.annotation.Mock;
import com.xiaomi.data.push.common.ClassUtils;
import com.xiaomi.data.push.common.Service;
import com.xiaomi.data.push.context.ServerInfo;
import com.xiaomi.data.push.dao.mapper.ActionConfMapper;
import com.xiaomi.data.push.error.ErrorService;
import com.xiaomi.data.push.log.ActionExecuteInfo;
import com.xiaomi.youpin.annotation.log.Log;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.lang.reflect.Method;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author goodjava@qq.com
 */
@Component
public class ActionContext implements Service {

    private static final Logger logger = LoggerFactory.getLogger(ActionContext.class);

    private volatile boolean shutdown = false;

    @Autowired
    private ActionConfMapper actionConfMapper;

    @Autowired
    private ErrorService errorService;

    @Autowired
    private ServerInfo serverInfo;

    @Autowired
    private ApplicationContext ac;

//    @NacosInjected(properties = @NacosProperties(encode = "UTF-8", serverAddr = "${nacos.config.addrs}"))
//    private ConfigService configService;


    private ConcurrentHashMap<String, ActionInfo> actionInfos = new ConcurrentHashMap<>();

    public ConcurrentHashMap<String, ActionExecuteInfo> actionMap = new ConcurrentHashMap<>();

    public ConcurrentHashMap<String, ActionInfo> getActionInfos() {
        return actionInfos;
    }

    private long lastDateTime = 0L;

    @Value("${support.action}")
    private boolean supportAction;

    @Value("${action.config.dataid}")
    private String configDataId;

    @Value("${action.config.group}")
    private String configGroup;




    /**
     * 启动的时候就会把定义的action入库
     */
    @PostConstruct
    @Override
    public void init() {
        if (!supportAction) {
            return;
        }

        ActionConfigData data = loadConfig();

        //从 spring 容器中加载出来的
        AtomicReference<List<Method>> list = new AtomicReference<>(ClassUtils.findMethodWithAnnotation(ac, Action.class));

        list.get().stream().forEach(m -> {
            Action action = m.getAnnotation(Action.class);
            ActionInfo actionInfo = new ActionInfo();
            if (null != action && StringUtils.isNotEmpty(action.name())) {
                actionInfo.setName(action.name());
            } else {
                actionInfo.setName(m.toString());
            }
            actionInfo.setMethodName(m.toString());

            actionMap.put(actionInfo.getName(), new ActionExecuteInfo());

            //获取配置中的信息
            ActionInfo c = data.getActionConfMap().get(actionInfo.getName());

            //有cache注解并且允许cache
            if (m.getAnnotation(Cache.class) != null) {
                if (null != c) {
                    actionInfo.setCache(c.isCache());
                }
            }

            //允许log
            if (m.getAnnotation(Log.class) != null) {
                if (null != c) {
                    actionInfo.setRecordLog(c.isRecordLog());
                }
            }

            //允许mock数据
            if (m.getAnnotation(Mock.class) != null) {
                if (null != c) {
                    actionInfo.setMock(c.isMock());
                }
            }

            //是否上线
            if (m.getAnnotation(Action.class) != null) {
                int version = m.getAnnotation(Action.class).version();
                actionInfo.setVersion(version);

                if (null != c) {
                    actionInfo.setOnline(c.getOnline());
                }
            }

            String serverInfo = this.serverInfo.toString();
            actionInfo.setServerInfo(serverInfo);
            actionInfo.setVersion(1);

            this.actionInfos.putIfAbsent(actionInfo.getMethodName(), actionInfo);


        });

        //save(data);

    }

    //用来生成测试数据的
    private void save(ActionConfigData data) {
        data.setTime(new Date().toString());
        data.setVersion("0.0.1");
        data.setActionConfMap(this.actionInfos);
        System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(data));

//        try {
//            configService.publishConfig(configDataId, configGroup, new GsonBuilder().setPrettyPrinting().create().toJson(data));
//        } catch (NacosException e) {
//            e.printStackTrace();
//        }
    }


    @Override
    public void schedule() {
        if (!supportAction) {
            return;
        }

        if (shutdown) {
            return;
        }
        long now = System.currentTimeMillis();
        //从配置获取最新的action配置
        loadConfigSchedule();
        //上报接口调用信息
        reportActionExecuteInfo(now, false);
    }

    private void loadConfigSchedule() {
        ActionConfigData data = loadConfig();
        data.getActionConfMap().entrySet().stream().forEach(it -> {
            if (this.actionInfos.containsKey(it.getValue().getMethodName())) {
                this.actionInfos.put(it.getValue().getMethodName(), it.getValue());
            }
        });
    }


    private ActionConfigData loadConfig() {
//        try {
//            String config = configService.getConfig(configDataId, configGroup, 2000);
//            ActionConfigData configData = new Gson().fromJson(config, ActionConfigData.class);
//            return configData;
//        } catch (NacosException e) {
//            logger.error("loadConfig error:{}", e.getMessage());
//        }
//        return new ActionConfigData();
        return null;
    }

    /**
     * 停服的时候会立刻上报
     *
     * @param now
     * @param immediate
     */
    private void reportActionExecuteInfo(long now, boolean immediate) {
        //错误信息的处理上传
//        if (immediate || now - lastDateTime >= TimeUnit.MINUTES.toMillis(1)) {
//            this.lastDateTime = now;
//            String serverInfo = this.serverInfo.toString();
//            logger.info("reportActionExecuteInfo serverInfo:{}", serverInfo);
//            //上报节点信息
//            try {
//                List<ApiEndpointInfo> endpointInfos = this.actionInfos.entrySet().stream().map(it -> {
//                    ActionInfo v = it.getValue();
//                    ApiEndpointInfo ai = new ApiEndpointInfo();
//                    ai.setAction(v.getName());
//                    ai.setMethod(v.getMethodName());
//                    ai.setMock(v.isMock() ? 1 : 0);
//                    ai.setNeedCache(v.isCache() ? 1 : 0);
//                    ai.setNeedLog(v.isRecordLog() ? 1 : 0);
//                    ai.setOnline((int) v.getOnline());
//                    ai.setUtime(this.lastDateTime);
//                    ai.setAddr(serverInfo);
//                    return ai;
//                }).collect(Collectors.toList());
//                HeartBeat hb = new HeartBeat();
//                hb.setEndpointInfos(endpointInfos);
//                endpointService.sendHeartBeat(hb);
//            } catch (Throwable ex) {
//                logger.error(ex.getMessage());
//            }
//
//
//            //上报错误信息
//            EndpointStatistics endpointStatistics = new EndpointStatistics();
//            List<ApiStatistics> statisticsList = actionMap.entrySet().stream().map(it -> {
//                ApiStatistics apiStatistics = new ApiStatistics();
//                apiStatistics.setAction(it.getKey());
//                apiStatistics.setFailure(it.getValue().getFailureNum().get());
//                apiStatistics.setSuccess(it.getValue().getSuccessNum().get());
//                apiStatistics.setSampleTime(now);
//                if (it.getValue().getExecuteNum().get() != 0L) {
//                    apiStatistics.setRoundTime((int) (it.getValue().getRt().get() / it.getValue().getExecuteNum().get()));
//                } else {
//                    apiStatistics.setRoundTime(0);
//                }
//                apiStatistics.setAddr(serverInfo);
//                return apiStatistics;
//            }).collect(Collectors.toList());
//
//            endpointStatistics.setStatisticsList(statisticsList);
//            try {
//                endpointService.sendStatistisc(endpointStatistics);
//            } catch (Throwable ex) {
//                logger.error(ex.getMessage());
//            }
//            //数据清空
//            actionMap.forEach((k, v) -> {
//                v.getFailureNum().set(0);
//                v.getSuccessNum().set(0);
//                v.getExecuteNum().set(0);
//                v.getRt().set(0);
//            });
//        }
    }


    @Override
    public void shutdown() {
        logger.info("ActionContext shutdown begin");
        shutdown = true;
        reportActionExecuteInfo(System.currentTimeMillis(), true);
        logger.info("ActionContext shutdown finish");
    }
}
