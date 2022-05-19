package com.xiaomi.youpin.docean.plugin.sql;

import com.xiaomi.youpin.docean.Aop;
import com.xiaomi.youpin.docean.Ioc;
import com.xiaomi.youpin.docean.anno.DOceanPlugin;
import com.xiaomi.youpin.docean.common.StringUtils;
import com.xiaomi.youpin.docean.plugin.IPlugin;
import com.xiaomi.youpin.docean.plugin.datasource.DatasourceConfig;
import com.xiaomi.youpin.docean.plugin.datasource.DatasourcePlugin;
import com.xiaomi.youpin.docean.plugin.datasource.anno.Transactional;
import com.xiaomi.youpin.docean.plugin.sql.interceptor.TransactionalInterceptor;
import lombok.extern.slf4j.Slf4j;

import javax.sql.DataSource;
import java.util.List;
import java.util.Set;


/**
 * @author goodjava@qq.com
 */
@DOceanPlugin
@Slf4j
public class SqlPlugin implements IPlugin {


    @Override
    public void init(Set<? extends Class<?>> classSet, Ioc ioc) {
        log.info("init dbplugin");
        Aop.ins().getInterceptorMap().put(Transactional.class, new TransactionalInterceptor());
        List<String> dbNames = ioc.getBean(DatasourcePlugin.DB_NAMES);
        if (dbNames.size() == 1) {
            DataSource ds = ioc.getBean(dbNames.get(0));
            JdbcTransaction tran = new JdbcTransaction(ds, null, true);
            Db db = new Db(ds, tran);
            ioc.putBean(db);
        } else {
            dbNames.stream().forEach(it -> addDAO(ioc, it));
        }
    }

    /**
     * 增加一个dao
     */
    public void add(DatasourceConfig config) {
        DatasourcePlugin datasourcePlugin = Ioc.ins().getBean(DatasourcePlugin.class);
        DataSource ds = datasourcePlugin.add(config);
        JdbcTransaction tran = new JdbcTransaction(ds, null, true);
        Db db = new Db(ds, tran);
        Ioc.ins().putBean("dao:" + config.getName(), db);
    }

    /**
     * 移除一个dao
     */
    public void remove(DatasourceConfig config) {
        Db db = Ioc.ins().getBean("dao:" + config.getName());
        DatasourcePlugin datasourcePlugin = Ioc.ins().getBean(DatasourcePlugin.class);
        Ioc.ins().remove("dao:" + config.getName());
        datasourcePlugin.remove(config);
    }

    private void addDAO(Ioc ioc, String beanName) {
        Object datasource = ioc.getBean(beanName);
        if (datasource instanceof DataSource) {
            DataSource ds = (DataSource) datasource;
            JdbcTransaction tran = new JdbcTransaction(ds, null, true);
            Db db = new Db(ds, tran);
            ioc.putBean("mione_" + beanName, db);
        }
    }

    @Override
    public String version() {
        return "0.0.2:2020-07-05";
    }
}
