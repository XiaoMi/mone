///*
// *  Copyright 2020 Xiaomi
// *
// *    Licensed under the Apache License, Version 2.0 (the "License");
// *    you may not use this file except in compliance with the License.
// *    You may obtain a copy of the License at
// *
// *        http://www.apache.org/licenses/LICENSE-2.0
// *
// *    Unless required by applicable law or agreed to in writing, software
// *    distributed under the License is distributed on an "AS IS" BASIS,
// *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// *    See the License for the specific language governing permissions and
// *    limitations under the License.
// */
//
//package com.xiaomi.youpin;
//
//import com.google.common.collect.Lists;
//import com.google.gson.Gson;
//import com.xiaomi.youpin.gwdash.bo.*;
//import com.xiaomi.youpin.gwdash.dao.model.*;
//import lombok.Data;
//import org.junit.Test;
//import org.nutz.dao.*;
//import org.nutz.dao.entity.Record;
//import org.nutz.dao.entity.annotation.ColDefine;
//import org.nutz.dao.entity.annotation.ColType;
//import org.nutz.dao.entity.annotation.Column;
//import org.nutz.dao.entity.annotation.Id;
//import org.nutz.dao.impl.NutDao;
//import org.nutz.dao.impl.SimpleDataSource;
//import org.nutz.dao.pager.Pager;
//import org.nutz.dao.sql.Sql;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
//public class DaoTest {
//
//    private Dao dao() throws ClassNotFoundException {
////        SimpleDataSource ds = new SimpleDataSource();
////        ds.setDriverClassName("com.mysql.jdbc.Driver");
////        ds.setJdbcUrl("jdbc:mysql://10.38.163.57:4100/mone?characterEncoding=utf8&useSSL=false");
////        ds.setUsername("");
////        ds.setPassword("");
////        Dao dao = new NutDao(ds);
////        return dao;
//        return null;
//    }
//
//    @Test
//    public void testCreate() throws ClassNotFoundException {
//        Dao dao = dao();
////        dao.create(TeslaDs.class,true);
////        dao.create(PluginInfoBo.class,true);
////        dao.create(PluginData.class,true);
////        dao.create(GatewayServerInfo.class, true);
//
////        dao.create(Project.class,true);
////        dao.create(ProjectRole.class,true);
//
////        dao.create(Approval.class, true);
////        dao.create(HttpService.class, true);
//        dao.create(MError.class, true);
//    }
//
//
//    @Test
//    public void testKey() throws ClassNotFoundException {
//        Dao dao = dao();
//        MError error = dao.fetch(MError.class, 1);
//        System.out.println(error);
//        error.setKey("key");
//        dao.update(error);
//
//
//        MError r = dao.fetch(MError.class, Cnd.where("key", "=", "key"));
//        System.out.println(r);
//    }
//
//
//    @Data
//    public static class T {
//        @Id
//        private int id;
//
//        @ColDefine(type = ColType.MYSQL_JSON)
//        @Column
//        private J j;
//    }
//
//
//    @Data
//    public static class J {
//        private String name;
//        private int age;
//    }
//
//
//    @Test
//    public void testQuery4() throws ClassNotFoundException {
//        Dao dao = dao();
//        List<T> list = dao.query(T.class, null);
//        System.out.println(list);
//    }
//
//
//    @Test
//    public void testInsert() throws ClassNotFoundException {
//        Dao dao = dao();
//        T t = new T();
//        J j = new J();
//        j.setAge(111);
//        j.setName("azzz");
//        t.setJ(j);
//        dao.insert(t);
//    }
//
//
//    @Test
//    public void testUpdate2() throws ClassNotFoundException {
//        Dao dao = dao();
//        Sql sql = Sqls.create("update t set j=json_set(j,'$.name','gogogog') where id = @id");
//        sql.params().set("id", 2);
//        dao.execute(sql);
//    }
//
//    @Test
//    public void testUpdate3() throws ClassNotFoundException {
//        Dao dao = dao();
//        Sql sql = Sqls.create("update t set a=@a where id = @id");
//        sql.params().set("id", 2);
//        sql.params().set("a", "[{\"id\":1},{\"id\":3}]");
//        dao.execute(sql);
//    }
//
//
//    @Test
//    public void testQuery() throws ClassNotFoundException {
//        Dao dao = dao();
//        List<TeslaDs> res = dao.query(TeslaDs.class, Cnd.where("id", "in", Lists.newArrayList(1, 2)));
//        System.out.println(res.size());
//    }
//
//    @Test
//    public void testQuery2() throws ClassNotFoundException {
//        Dao dao = dao();
//        PluginData data = dao.fetch(PluginData.class, Cnd.where("plugin_id", "=", 2).orderBy("utime", "desc"));
//        System.out.println(data.getId());
//
//    }
//
//    @Test
//    public void testUpdate() throws ClassNotFoundException {
//        Dao dao = dao();
//        dao.update(PluginData.class, Chain.make("id", 100), Cnd.where("id", "=", 6));
//    }
//
//
//    @Test
//    public void testUpdate11() throws ClassNotFoundException {
//        Dao dao = dao();
//        Sql sql = Sqls.create("update project_pipeline set deploy_info=json_set(deploy_info,'$.status','1','$.utime',@utime)");
//        sql.params().set("utime", 1111);
//        DeployInfo deployInfo = new DeployInfo();
//        deployInfo.setStatus(2);
//        sql.params().set("info", new Gson().toJson(deployInfo));
//        dao.execute(sql);
//    }
//
//    @Test
//    public void testUpdate12() throws ClassNotFoundException {
//        Dao dao = dao();
//        Sql sql = Sqls.create("update project_pipeline set deploy_info=@info");
//        DeployInfo deployInfo = new DeployInfo();
//        deployInfo.setStatus(2);
//        sql.params().set("info", null);
//        dao.execute(sql);
//    }
//
//
//    @Test
//    public void testQuery3() throws ClassNotFoundException {
//        Dao dao = dao();
////        dao.query(GatewayServerInfo.class,null)
//
//        List<Record> list = dao.query("gateway_server_info", null, null, "distinct `group`");
//        System.out.println(list);
//        System.out.println(list.get(0).getString("group"));
//    }
//
//    @Test
//    public void testJoinQuery() throws ClassNotFoundException {
//        Dao dao = dao();
//        Sql sql = Sqls.queryRecord("select a.id aid,p.id pid from approval a join project p on a.projectId=p.id where a.id=@id");
//        sql.params().set("id", "1");
//        dao.execute(sql);
//        List<Map> list = sql.getList(Map.class);
//        System.out.println(list);
//    }
//
//
//    /**
//     * select
//     *
//     * @throws ClassNotFoundException
//     */
//    @Test
//    public void testLabels() throws ClassNotFoundException {
//        Dao dao = dao();
//        Machine data = dao.fetch(Machine.class, 1);
//        System.out.println(data);
//    }
//
//
//    /**
//     * update
//     *
//     * @throws ClassNotFoundException
//     */
//    @Test
//    public void testLabels2() throws ClassNotFoundException {
//        Dao dao = dao();
//        MachineLabels labels = new MachineLabels();
//        labels.put("1", "a");
//        labels.put("2", "b");
//        Sql sql = Sqls.create("update machine_list set labels=@labels where id =1");
//        sql.params().set("labels", new Gson().toJson(labels));
//        dao.execute(sql);
//    }
//
//    /**
//     * insert
//     *
//     * @throws ClassNotFoundException
//     */
//    @Test
//    public void testLabels3() throws ClassNotFoundException {
//        Dao dao = dao();
//        Sql sql = Sqls.create("update machine_list set labels=json_insert(labels,$key,@value) where id =1");
//        sql.vars().set("key", "'$.key'");
//        sql.params().set("value", "value");
//        dao.execute(sql);
//    }
//
//
//    /**
//     * remove
//     *
//     * @throws ClassNotFoundException
//     */
//    @Test
//    public void testLabels4() throws ClassNotFoundException {
//        Dao dao = dao();
//        Sql sql = Sqls.create("update machine_list set labels=json_remove(labels,'$.e') where id =1");
//        dao.execute(sql);
//    }
//
//    /**
//     * label 查询
//     *
//     * @throws ClassNotFoundException
//     */
//    @Test
//    public void testLabels5() throws ClassNotFoundException {
//        Dao dao = dao();
//        Sql sql = Sqls.create("select * from machine_list where labels->'$.key'='value'");
//        sql.setCallback((conn, rs, sql1) -> {
//
//            List<Machine> list = new ArrayList<>();
//
//            while (rs.next()) {
//                Machine m = new Machine();
//                m.setId(rs.getLong("id"));
//                m.setLabels(new Gson().fromJson(rs.getString("labels"), MachineLabels.class));
//                list.add(m);
//            }
//            return list;
//        });
//        List<Machine> list = dao.execute(sql).getList(Machine.class);
//        System.out.println(list);
//    }
//
//
//    @Test
//    public void testLabels6() throws ClassNotFoundException {
//        Dao dao = dao();
//        Sql sql = Sqls.create("select * from machine_list where labels->$key = @value");
//        String key = "key";
//        String value = "value";
//        sql.vars().set("key", String.format("'$.%s'", key));
//        sql.params().set("value", value);
//        sql.setCallback(Sqls.callback.entities());
//        sql.setEntity(dao.getEntity(Machine.class));
//        List<Machine> list = dao.execute(sql).getList(Machine.class);
//        System.out.println(list);
//    }
//
//
//    /**
//     * insert or update
//     *
//     * @throws ClassNotFoundException
//     */
//    @Test
//    public void testLabels7() throws ClassNotFoundException {
//        Dao dao = dao();
//        Sql sql = Sqls.create("update machine_list set labels=json_set(labels,$key,@value) where id =1");
//        String key = "key11";
//        String value = "value11";
//        sql.vars().set("key", String.format("'$.%s'", key));
//        sql.params().set("value", value);
//        dao.execute(sql);
//    }
//
//
//    /**
//     * 获取label 信息
//     *
//     * @throws ClassNotFoundException
//     */
//    @Test
//    public void testLabels8() throws ClassNotFoundException {
//        Dao dao = dao();
//        MachineLabels labels = dao.fetch(Machine.class, Cnd.where("ip", "=", "10.38.163.223")).getLabels();
//        System.out.println(labels);
//    }
//
//
//
//    @Test
//    public void testPipe() throws ClassNotFoundException {
//        ProjectPipeline pipeline = new ProjectPipeline();
//        dao().insert(pipeline);
//    }
//
//    @Test
//    public void testUpdatePipeline() throws ClassNotFoundException {
//        Dao dao = dao();
//        ProjectPipeline pp = dao.fetch(ProjectPipeline.class, 1819);
//        pp.setUsername("zzy1");
//        pp.setVersion(0);
//        int res = dao.updateWithVersion(pp);
//        System.out.println(res);
//    }
//
//
//    @Test
//    public void testUpdatePipeline2() throws ClassNotFoundException {
//        Dao dao = dao();
//        ProjectPipeline pp = dao.fetch(ProjectPipeline.class, 1819);
//        pp.setUsername("zzy4");
//        pp.setStatus(4);
//        pp.setProjectId(1L);
////        pp.setVersion(0);
//        int res = dao.updateWithVersion(pp, FieldFilter.create(ProjectPipeline.class,"username|status"));
//        System.out.println(res);
//    }
//
//    @Test
//    public void updateMilogName() throws ClassNotFoundException {
//        Dao dao = dao();
//        List<Machine> machineList = dao.query(Machine.class, null);
//        for (Machine machine : machineList) {
//            if (machine.getLabels().get(MachineLabels.Apps).contains("log-agent")) {
//                String s = machine.getLabels().get(MachineLabels.Apps).replaceAll("log-agent", "milog-agent");
//                MachineLabels labels = machine.getLabels();
//                labels.put(MachineLabels.Apps, s);
//                machine.setLabels(labels);
//                System.out.println(machine);
////                dao.update(machine);
//            }
//        }
//    }
//
//
//
//}
