package com.xiaomi.youpin.docean.plugin.db.interceptor;

import com.xiaomi.youpin.docean.plugin.datasource.DatasourceConfig;
import com.xiaomi.youpin.docean.plugin.db.po.DbRequest;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.nutz.dao.DaoException;
import org.nutz.dao.DaoInterceptor;
import org.nutz.dao.DaoInterceptorChain;
import org.nutz.dao.impl.jdbc.NutPojo;
import org.nutz.dao.sql.SqlType;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

/**
 * @Author goodjava@qq.com
 * @Date 2021/1/24 20:02
 * <p>
 * 这里的sql未来会发往mesh服务器,哪里向mysql发送
 */
@Slf4j
public class DoceanInterceptor implements DaoInterceptor {

    @Setter
    private NutzDaoMeshInvoker meshInvoker;

    @Setter
    private DatasourceConfig datasourceConfig;

    @Override
    public void filter(DaoInterceptorChain chain) throws DaoException {
        String statement = chain.getDaoStatement().toString();
        String ds = ds(chain);
        log.info("nutz dao mesh filter sql:{}", statement);
        if (null != meshInvoker) {
            NutPojo np = (NutPojo) chain.getDaoStatement();
            if (np.getSqlType().equals(SqlType.UPDATE)
                    || np.getSqlType().equals(SqlType.INSERT)
                    || np.getSqlType().equals(SqlType.DELETE)) {
                int n = meshInvoker.update(DbRequest.builder()
                        .entity(np.getEntity())
                        .sql(statement)
                        .dsName(datasourceConfig.getName())
                        .type(np.getSqlType().name())
                        .datasource(ds)
                        .build());
                np.getContext().setUpdateCount(n);
                setProperty(chain, "updateCount", n);
            } else {
                List list = meshInvoker.query(DbRequest.builder()
                        .sql(statement)
                        .dsName(datasourceConfig.getName())
                        .entity(np.getEntity())
                        .type(np.getSqlType().name())
                        .datasource(ds)
                        .build());
                np.getContext().setResult(list);
            }
        } else {
            chain.doChain();
        }
    }

    @SneakyThrows
    private String ds(DaoInterceptorChain chain) {
        String url = chain.getConnection().getMetaData().getURL();
        String ds = Arrays.stream(url.split("&")).filter(it -> it.startsWith("mds=")).findFirst().orElse("mds=default").split("=")[1];
        return ds;
    }


    @SneakyThrows
    protected void setProperty(Object obj, String property, Object value) {
        if (null != value) {
            Field field = obj.getClass().getDeclaredField(property);
            field.setAccessible(true);
            field.set(obj, value);
        }
    }
}
