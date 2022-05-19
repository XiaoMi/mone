package com.xiaomi.youpin.docean.plugin.datasource;

import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.anno.DOceanPlugin;
import com.xiaomi.youpin.docean.plugin.IPlugin;
import com.xiaomi.youpin.docean.plugin.config.Config;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author goodjava@qq.com
 * 把框架中的ds注入到业务容器当中
 */
@Slf4j
@DOceanPlugin(order = 11)
public class ServerLessDatasourcePlugin implements IPlugin {

    public static final String DB_NAMES = "DB_NAMES";

    public static final String SERVER_LESS_DATASOURCE_LIST = "server_less_datasource_list";

    @Override
    public void init(Set<? extends Class<?>> classSet, Ioc ioc) {
        log.info("init datasource plugin");
        if (ioc.containsBean(SERVER_LESS_DATASOURCE_LIST)) {
            List<String> dbNames = new ArrayList();
            Config c = ioc.getBean(Config.class);
            initByDsNames(ioc, c, dbNames);
            ioc.putBean(DB_NAMES, dbNames);
        }
    }

    private void initByDsNames(final Ioc ioc, final Config c, final List<String> dbNames) {
        List<DatasourceWrapper> list = ioc.getBean(SERVER_LESS_DATASOURCE_LIST);
        if (null == list || list.size() == 0) {
            return;
        }
        list.forEach(ds -> {
            String dbName = ds.getName() + "_serverless_ds";
            ioc.putBean(dbName, ds.getDs());
            DatasourceConfig config = new DatasourceConfig();
            config.setName(ds.getName());
            ioc.putBean(dbName + "_config", config);
            dbNames.add(dbName);
        });
    }


    @Override
    public String version() {
        return "0.0.1:goodjava@qq.com:2022-03-28";
    }

}
