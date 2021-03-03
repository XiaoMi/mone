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

package com.xiaomi.youpin.docean.plugin.test;

import com.xiaomi.youpin.docean.anno.Component;
import lombok.extern.slf4j.Slf4j;
import org.influxdb.InfluxDB;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import org.influxdb.dto.QueryResult;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.Resource;
import java.util.Random;

/**
 * @author zhangjunyi
 * created on 2020/8/17 11:45 上午
 */
@Component
@Slf4j
public class TinfluxDb {
    @Resource
    private InfluxDB influxDB;
    private static final String measurement = "test_measurement";
    public void insert(int num) {

        Point.Builder builder = Point.measurement(measurement);  // 创建Builder，设置表名
        builder.addField("count", num);  // 添加Field
        builder.tag("TAG_CODE", "TAG_VALUE_" + num);    // 添加Tag
        Point point = builder.build();
        log.info("add to influxdb :{}", point);
        influxDB.write(point);
    }
    public void doInsert() {
        Random random = new Random();
        insert(random.nextInt(3000));
    }

    public QueryResult getList() {
        QueryResult queryResult = influxDB.query(new Query("SELECT * FROM " + measurement + " order by time desc LIMIT 100", "my_test2"));
        return queryResult;
    }
}
