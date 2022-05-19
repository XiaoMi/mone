package com.xiaomi.youpin.docean.plugin.test.db;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.xiaomi.youpin.docean.plugin.db.interceptor.DoceanInterceptor;
import com.xiaomi.youpin.docean.plugin.db.interceptor.NutzDaoMeshInvoker;
import com.xiaomi.youpin.docean.plugin.db.po.DbRequest;
import com.xiaomi.youpin.docean.plugin.test.common.DbUtils;
import lombok.SneakyThrows;
import org.junit.Test;
import org.nutz.dao.Cnd;
import org.nutz.dao.entity.Entity;
import org.nutz.dao.entity.MappingField;
import org.nutz.dao.impl.NutDao;
import org.nutz.dao.impl.NutTxDao;
import org.nutz.dao.impl.entity.NutEntity;
import org.springframework.util.Assert;

import java.beans.PropertyVetoException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * @author goodjava@qq.com
 * @date 2020/7/2
 */
public class DbTest {


    @Test
    public void testDb() throws PropertyVetoException {
        NutDao dao = new NutDao(DbUtils.datasource());
        DoceanInterceptor interceptor = new DoceanInterceptor();
        dao.addInterceptor(interceptor);
        NutTxDao td = new NutTxDao(dao);

        td.beginRC();
        td.insert(new com.xiaomi.youpin.docean.plugin.test.bo.Test());
        td.commit();
    }


    @Test
    public void testDelete() throws PropertyVetoException {
        NutDao dao = new NutDao(DbUtils.datasource());
        DoceanInterceptor interceptor = new DoceanInterceptor();
        interceptor.setMeshInvoker(new NutzDaoMeshInvoker() {
            @Override
            public List query(DbRequest request) {
                return Lists.newArrayList();
            }

            @Override
            public int update(DbRequest request) {
                return 1;
            }
        });
        dao.addInterceptor(interceptor);
        int n = dao.delete(com.xiaomi.youpin.docean.plugin.test.bo.Test.class,1L);
        System.out.println(n);
    }


    @Test
    public void testQuery() throws PropertyVetoException {
        NutDao dao = new NutDao(DbUtils.datasource());
        DoceanInterceptor interceptor = new DoceanInterceptor();
        interceptor.setMeshInvoker(new NutzDaoMeshInvoker() {
            @Override
            public List query(DbRequest request) {
                Map<String,String> m = Maps.newHashMap();
                m.put("id","666");
                m.put("test","t");
                NutEntity entity = (NutEntity) request.getEntity();
                Class clazz = entity.getType();
                Object obj = obj(clazz);
                List<MappingField> fields = entity.getMappingFields();
                fields.stream().forEach(f->{
                    Object v = m.get(f.getColumnName());
                    setProperty(obj,f.getName(),value(v.toString(),f.getType()));
                });

                return Lists.newArrayList(obj);
            }

            @Override
            public int update(DbRequest request) {
                return 0;
            }
        });
        dao.addInterceptor(interceptor);
        List<com.xiaomi.youpin.docean.plugin.test.bo.Test> list =
                dao.query(com.xiaomi.youpin.docean.plugin.test.bo.Test.class, Cnd.where("id","=","1"));
        System.out.println(list);
        Assert.isTrue(list.size() > 0, "<0");
    }

    @SneakyThrows
    private Object obj(Class clazz) {
        Object ins = clazz.newInstance();
        return ins;
    }

    @SneakyThrows
    private void setProperty(Object obj, String property, Object value) {
        Field field = obj.getClass().getDeclaredField(property);
        field.setAccessible(true);
        field.set(obj, value);
    }

    private Object value(String v, Type type) {
        if (type.equals(Integer.class) || type.equals(int.class)) {
            if (null == v) {
                return 0;
            }
            return Integer.valueOf(v);
        }
        if (type.equals(Long.class)) {
            if (null == v) {
                return 0L;
            }
            return Long.valueOf(v);
        }
        if (type.equals(String.class)) {
            return v;
        }
        return null;
    }
}
