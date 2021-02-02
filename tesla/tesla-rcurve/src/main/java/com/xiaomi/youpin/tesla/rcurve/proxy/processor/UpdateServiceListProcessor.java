package com.xiaomi.youpin.tesla.rcurve.proxy.processor;

import com.alibaba.nacos.api.naming.pojo.Instance;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xiaomi.data.push.common.Send;
import com.xiaomi.data.push.uds.UdsServer;
import com.xiaomi.data.push.uds.po.UdsCommand;
import com.xiaomi.data.push.uds.processor.UdsProcessor;
import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.anno.Component;
import com.xiaomi.youpin.docean.common.StringUtils;
import com.xiaomi.youpin.docean.plugin.datasource.DatasourceConfig;
import com.xiaomi.youpin.docean.plugin.dmesh.ds.Datasource;
import com.xiaomi.youpin.docean.plugin.dmesh.service.MeshServiceConfig;
import com.xiaomi.youpin.docean.plugin.redis.RedisDsConfig;
import com.xiaomi.youpin.docean.plugin.redis.RedisPlugin;
import com.xiaomi.youpin.docean.plugin.sql.SqlPlugin;
import com.xiaomi.youpin.tesla.rcurve.proxy.common.NetUtils;
import com.xiaomi.youpin.tesla.rcurve.proxy.manager.ActorManager;
import com.xiaomi.youpin.tesla.rcurve.proxy.manager.DsManager;
import com.xiaomi.youpin.tesla.rcurve.proxy.manager.ServiceManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.common.utils.ConfigUtils;

import javax.annotation.Resource;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author goodjava@qq.com
 * 处理传输过来的服务列表
 */
@Component
@Slf4j
public class UpdateServiceListProcessor implements UdsProcessor {

    private Gson gson = new Gson();

    private String ip;

    private int port;

    @Resource
    private DsManager dsManager;

    @Resource
    private ServiceManager serviceManager;


    @Resource
    private ActorManager actorManager;

    public void init() {
        UdsServer server = Ioc.ins().getBean(UdsServer.class);
        server.getProcessorMap().put("updateServerList", this);

        String hostToRegistry = ConfigUtils.getSystemProperty("TESLA_HOST");
        if (StringUtils.isEmpty(hostToRegistry)) {
            hostToRegistry = NetUtils.getLocalHost();
        }
        ip = hostToRegistry;

        port = 7777;
    }

    @Override
    public void processRequest(UdsCommand udsCommand) {

        if (udsCommand.getServiceName().equals("init")) {
            log.info("app init");
            initMesh(udsCommand);
            //初始化这个应用的actor
            actorManager.regApp(udsCommand.getApp());
            return;
        }

        Type typeOfT = new TypeToken<List<MeshServiceConfig>>() {
        }.getType();

        String data = udsCommand.getData(String.class);
        List<MeshServiceConfig> list = gson.fromJson(data, typeOfT);

        String utime = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date());
        list.stream().forEach(it -> {
            Instance instance = new Instance();
            instance.setIp(ip);
            instance.setPort(port);
            Map<String, String> metaData = getMetaData(utime, it);
            instance.setMetadata(metaData);
            serviceManager.reg(serviceName(it.getServiceName(), it.getGroup(), it.getVersion()), instance);
        });

        UdsCommand res = UdsCommand.createResponse(udsCommand);
        Send.send(udsCommand.getChannel(), res);
    }

    private Map<String, String> getMetaData(String utime, MeshServiceConfig it) {
        Map<String, String> metaData = Maps.newHashMap();
        metaData.put("side", "provider");
        metaData.put("dubbo", "2.0.2");
        metaData.put("interface", it.getServiceName());
        metaData.put("dubbo_version", "2.7.0_0.0.1_2020-11-25");
        metaData.put("protocol", "dubbo");
        metaData.put("application", it.getApp());
        metaData.put("dynamic", "true");
        metaData.put("category", "providers");
        metaData.put("anyhost", "true");
        metaData.put("group", it.getGroup());
        metaData.put("version", it.getVersion());
        metaData.put("timestamp", String.valueOf(System.currentTimeMillis()));
        metaData.put("mesh", "true");
        metaData.put("mesh_app", it.getApp());
        metaData.put("mesh_utime", utime);
        return metaData;
    }


    /**
     * 初始化mesh 的数据源
     *
     * @param udsCommand
     */
    private void initMesh(UdsCommand udsCommand) {
        String data = udsCommand.getData(String.class);
        Type typeOfT = new TypeToken<Map<String, Datasource>>() {
        }.getType();
        Map<String, Datasource> m = new Gson().fromJson(data, typeOfT);


        List<Datasource> list = m.entrySet().stream().map(it -> it.getValue()).collect(Collectors.toList());
        dsManager.add(udsCommand.getApp(), list);

        if (m.containsKey("mysql")) {
            Datasource v = m.get("mysql");
            SqlPlugin sqlPlugin = Ioc.ins().getBean(SqlPlugin.class);
            DatasourceConfig config = new DatasourceConfig();
            config.setName(udsCommand.getApp());
            config.setDefaultMinPoolSize(v.getDefaultMinPoolSize());
            config.setDefaultMaxPoolSize(v.getDefaultMaxPoolSize());
            config.setDefaultInitialPoolSize(v.getDefaultInitialPoolSize());
            config.setDataSourceUserName(v.getDataSourceUserName());
            config.setDataSourcePasswd(v.getDataSourcePasswd());
            config.setDataSourceUrl(v.getDataSourceUrl());
            sqlPlugin.add(config);
            Send.sendMessage(udsCommand.getChannel(), "init mysql success");
        }

        if (m.containsKey("redis")) {
            Datasource v = m.get("redis");
            RedisPlugin redisPlugin = Ioc.ins().getBean(RedisPlugin.class);
            RedisDsConfig config = new RedisDsConfig();
            config.setName(udsCommand.getApp());
            config.setHosts(v.getHosts());
            if (null == v.getType()) {
                v.setType("");
            }
            config.setType(v.getType());
            redisPlugin.add(config);
            Send.sendMessage(udsCommand.getChannel(), "init redis success");
        }

    }

    private String serviceName(String serviceName, String group, String version) {
        StringBuilder sb = new StringBuilder("providers:").append(serviceName);
        if (StringUtils.isNotEmpty(group)) {
            sb.append(":").append(group);
        }
        if (StringUtils.isNotEmpty(version)) {
            sb.append(":").append(version);
        }
        return sb.toString();
    }
}
