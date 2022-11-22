/*
 *  Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.xiaomi.youpin.gwdash.dao;

import com.xiaomi.youpin.gwdash.bo.SonarQubeBo;
import com.xiaomi.youpin.gwdash.bo.SonarQubeParam;
import com.xiaomi.youpin.gwdash.common.SonarQubeStatusEnum;
import com.xiaomi.youpin.gwdash.dao.model.SonarQubeConfig;
import lombok.extern.slf4j.Slf4j;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.Sqls;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.sql.SqlCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author zheng.xucn@outlook.com
 */

@Slf4j
@Repository
public class SonarQubeConfigDao {
    @Autowired
    private Dao dao;


    public void delete(SonarQubeConfig config) {
        dao.delete(config);
    }


    public void update(SonarQubeConfig config) {
        config.setUtime(System.currentTimeMillis());
        dao.update(config);
    }

    public List<SonarQubeConfig> getAllSonarQubeConfigs() {
        return dao.query(SonarQubeConfig.class, null);
    }

    public SonarQubeConfig getSonarQubeConfig(long projectId) {
        return dao.fetch(SonarQubeConfig.class, Cnd.where("project_id", "=", projectId));
    }

    public void update(SonarQubeConfig config, SonarQubeParam sonarQubeParam, long taskId) {
        config.setUtime(System.currentTimeMillis());
        config.setBranch(sonarQubeParam.getBranch());
        config.setProfile(sonarQubeParam.getProfile());
        config.setProjectKey(sonarQubeParam.getProjectKey());
        config.setTaskId(taskId);
        config.setBuildPath(sonarQubeParam.getBuildPath());
        dao.update(config);
    }

    public void insert(SonarQubeParam sonarQubeParam, int taskId) {
        long now = System.currentTimeMillis();

        SonarQubeConfig config = new SonarQubeConfig();
        config.setProjectId(sonarQubeParam.getProjectId());
        config.setBranch(sonarQubeParam.getBranch());
        config.setProjectKey(sonarQubeParam.getProjectKey());
        config.setProfile(sonarQubeParam.getProfile());
        config.setStatus(SonarQubeStatusEnum.ON.getCode());
        config.setBuildPath(sonarQubeParam.getBuildPath());
        config.setTaskId(taskId);
        config.setCtime(now);
        config.setUtime(now);

        dao.insert(config);

    }

    public List<SonarQubeBo> getSonarqubeConfigsAndProjects() {
        Sql sql = Sqls.create("select * from sonarqube_config as A join project as P on A.project_id = P.id");
        sql.setCallback(new SqlCallback() {
            @Override
            public Object invoke(Connection connection, ResultSet resultSet, Sql sql) throws SQLException {
                List<SonarQubeBo> list = new LinkedList<>();
                while (resultSet.next()) {
                    SonarQubeBo bo = new SonarQubeBo();
                    int projectId = resultSet.getInt("project_id");
                    String branch = resultSet.getString("branch");
                    String projectKey = resultSet.getString("project_key");
                    String profile = resultSet.getString("profile");
                    int status = resultSet.getInt("status");
                    String buildPath = resultSet.getString("build_path");
                    String projectName = resultSet.getString("name");

                    bo.setProjectId(projectId);
                    bo.setBranch(branch);
                    bo.setProjectKey(projectKey);
                    bo.setProfile(profile);
                    bo.setStatus(status);
                    bo.setBuildPath(buildPath);
                    bo.setProjectName(projectName);

                    list.add(bo);
                }
                return list;
            }
        });
        dao.execute(sql);
        return sql.getList(SonarQubeBo.class);
    }


    public List<Integer> getProjectMembers(long projectId) {

        Sql sql = Sqls.create("select accountId from project_role where projectId=@projectId");

        sql.params().set("projectId", projectId);

        sql.setCallback(new SqlCallback() {
            @Override
            public Object invoke(Connection connection, ResultSet resultSet, Sql sql) throws SQLException {
                List<Integer> list = new ArrayList<>();
                while (resultSet.next()) {
                    int accountId = resultSet.getInt("accountId");
                    list.add(accountId);
                }
                return list;
            }
        });
        dao.execute(sql);
        return sql.getList(Integer.class);
    }
}
