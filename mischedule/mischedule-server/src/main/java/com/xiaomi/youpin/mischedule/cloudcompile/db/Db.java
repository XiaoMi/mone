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

package com.xiaomi.youpin.mischedule.cloudcompile.db;

import com.google.gson.Gson;
import com.xiaomi.youpin.mischedule.api.service.bo.CompileResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.annotation.PostConstruct;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

/**
 * @author gaoyibo
 */
@Slf4j
@Configuration
public class Db {

    @Value("${spring.datasource.url}")
    private String url;

    @Value("${spring.datasource.username}")
    private String userName;

    @Value("${spring.datasource.password}")
    private String password;

    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    private void init() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl(url);
        dataSource.setUsername(userName);
        dataSource.setPassword(password);
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public CompileResult query(Long sourceId) {
        if (sourceId == null) {
            return null;
        }
        return jdbcTemplate.queryForObject("select * from compile_result where source_id = ?", new CompileResultRowMapper(), sourceId);
    }

    public boolean insert(CompileResult result) {
        if (result == null) {
            return false;
        }
        if (result.getStartTime() == null) {
            result.setStartTime(new Date());
        }
        if (result.getEndTime() == null) {
            result.setEndTime(new Date());
        }
        String sql = "insert into compile_result (ks_key,step,status,start_time,end_time,creator,source_id) values (?,?,?,?,?,?,?)";
        int count=0;
        try {
            count = jdbcTemplate.update(sql, result.getKsKey(), result.getStep(),
                    result.getStatus(), result.getStartTime(), result.getEndTime(), result.getCreator(), result.getSourceId());
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
        if (count != 1) {
            return false;
        }
        return true;
    }

    public boolean updateById(CompileResult result) {
        if (result == null || result.getSourceId() == null) {
            return false;
        }
        if (result.getEndTime() == null) {
            result.setEndTime(new Date());
        }
        String sql = "update compile_result set step=?, status = ?, end_time = ? ,url= ?, log_url=? where source_id = ?";
        int count=0;
        try {
            count = jdbcTemplate.update(sql, result.getStep(), result.getStatus(),
                    result.getEndTime(), result.getUrl(), result.getLogUrl(),result.getSourceId());
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
        if (count != 1) {
            return false;
        }
        return true;
    }

    public static void main(String[] args) {
        Db db = new Db();
        db.init();
        CompileResult result = new CompileResult();
        result.setCreator("lyc");
        result.setKsKey("key1");
        result.setSourceId(111L);
        System.out.println(db.insert(result));

        System.out.println(new Gson().toJson(db.query(111L)));

        db.updateById(result);
        System.out.println(new Gson().toJson(db.query(111L)));
    }
}

class CompileResultRowMapper implements RowMapper<CompileResult> {
    @Override
    public CompileResult mapRow(ResultSet rs, int rowNum) throws SQLException {

        CompileResult compileResult = new CompileResult();
        compileResult.setId(rs.getInt("id"));
        compileResult.setKsKey(rs.getString("ks_key"));
        compileResult.setUrl(rs.getString("url"));
        compileResult.setUrl(rs.getString("log_url"));
        compileResult.setStep(rs.getInt("step"));
        compileResult.setStatus(rs.getInt("status"));
        compileResult.setStartTime(rs.getDate("start_time"));
        compileResult.setEndTime(rs.getDate("end_time"));
        compileResult.setSourceId(rs.getLong("source_id"));
        return compileResult;
    }
}
