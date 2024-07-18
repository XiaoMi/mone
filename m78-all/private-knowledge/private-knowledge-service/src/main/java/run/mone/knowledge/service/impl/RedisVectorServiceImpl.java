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

package run.mone.knowledge.service.impl;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.xiaomi.data.push.redis.Redis;
import org.apache.commons.lang3.tuple.Pair;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import run.mone.knowledge.api.dto.VectorData;
import run.mone.knowledge.service.RedisVectorService;

import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author shanwb
 * @date 2024-02-06
 */
@Service
@Slf4j
public class RedisVectorServiceImpl implements RedisVectorService {
    @Autowired
    private Redis redis;

    private Gson gson = new Gson();

    private static Type typeOfT = new TypeToken<Map<String, double[]>>() {
    }.getType();


    //将List<VectorData> vectorDataList 转换为Map<String, Map<String, double[]>> dataMap 并存入redis (class)
    @Override
    public boolean storeVectorByGroup(List<VectorData> vectorDataList) {
        Map<String, Map<String, double[]>> dataMap = new HashMap<>();

        for (VectorData vectorData : vectorDataList) {
            String outerKey = vectorData.getGroupKey();
            String innerKey = vectorData.getLeaf();
            double[] values = vectorData.getVector();

            if (!dataMap.containsKey(outerKey)) {
                dataMap.put(outerKey, new HashMap<>());
            }

            dataMap.get(outerKey).put(innerKey, values);
        }

        for (Map.Entry<String, Map<String, double[]>> entry : dataMap.entrySet()) {
            String groupKey = entry.getKey();
            Map<String, double[]> data = entry.getValue();
            String jsonData = gson.toJson(data);

            redis.set(groupKey, jsonData);
        }

        return true;
    }

    /**
     * 更新向量结果
     *
     * @param vectorDataList 向量数据列表
     * @return 是否更新成功
     */
    @Override
    public boolean updateVectorByLeaf(List<VectorData> vectorDataList) {
        Map<String, Map<String, double[]>> dataMap = new HashMap<>();

        for (VectorData vectorData : vectorDataList) {
            String outerKey = vectorData.getGroupKey();
            String innerKey = vectorData.getLeaf();
            double[] values = vectorData.getVector();

            if (!dataMap.containsKey(outerKey)) {
                dataMap.put(outerKey, new HashMap<>());
            }

            dataMap.get(outerKey).put(innerKey, values);
        }

        //查询已存在向量结果
        Map<String, Map<String, double[]>> existsDataMap = listByGroup2(new ArrayList<>(dataMap.keySet()));

        //更新向量结果
        for (Map.Entry<String, Map<String, double[]>> entry : dataMap.entrySet()) {
            String groupKey = entry.getKey();
            Map<String, double[]> updateData = entry.getValue();
            Map<String, double[]> existsData = existsDataMap.get(groupKey);
            if (null != existsData) {
                existsData.putAll(updateData);
            } else {
                existsDataMap.put(groupKey, updateData);
            }
        }

        //更新回redis
        for (Map.Entry<String, Map<String, double[]>> entry : existsDataMap.entrySet()) {
            String groupKey = entry.getKey();
            Map<String, double[]> data = entry.getValue();
            String jsonData = gson.toJson(data);

            redis.set(groupKey, jsonData);
        }

        return true;
    }

	@Override
    public List<VectorData> listByGroup(List<String> groupKeyList) {
        List<VectorData> vectorDataList = new ArrayList<>();
        Map<String, String> groupListResult = redis.mget(groupKeyList);
        if (null == groupListResult) {
            return Lists.newArrayList();
        }

        for (Map.Entry<String, String> entry : groupListResult.entrySet()) {
            String groupKey = entry.getKey();
            Pair<String, String> pair = VectorData.parseGroupKey(groupKey);
            String value = entry.getValue();

            Map<String, double[]> map = gson.fromJson(value, typeOfT);
            if (null == map || map.isEmpty()){
                continue;
            }
            for (Map.Entry<String, double[]> en : map.entrySet()) {
                VectorData vectorData = new VectorData();
                vectorData.setType(pair.getKey());
                vectorData.setGroup(pair.getValue());
                vectorData.setLeaf(en.getKey());
                vectorData.setVector(en.getValue());

                vectorDataList.add(vectorData);
            }
        }

        return vectorDataList;
    }

    /**
     * 根据提供的分组列表从Redis中获取数据，将每个分组的JSON字符串转换为Map<String, double[]>并返回一个嵌套的Map结构。
     */
    @Override
    public Map<String, Map<String, double[]>> listByGroup2(List<String> groupKeyList) {
        Map<String, Map<String, double[]>> resultMap = new HashMap<>();
        Map<String, String> groupListResult = redis.mget(groupKeyList);
        if (null == groupListResult) {
            return resultMap;
        }

        for (Map.Entry<String, String> entry : groupListResult.entrySet()) {
            String groupKey = entry.getKey();
            String value = entry.getValue();

            Map<String, double[]> map = gson.fromJson(value, typeOfT);
            resultMap.put(groupKey, map);
        }

        return resultMap;
    }

    //基于VectorData的group列表进行redis数据删除，入参是List<String> groupList (class)
    @Override
    public boolean deleteByGroup(List<String> groupKeyList) {
        for (String groupKey : groupKeyList) {
            redis.del(groupKey);
        }
        return true;
    }

    //基于VectorData的leaf列表进行redis数据删除，需要基于入参聚合出group集合，然后从redis中查出数据，并基于leaf进行数据删除,入参是List<VectorData> vectorDataList (class)
    @Override
    public boolean deleteByLeaf(List<VectorData> vectorDataList) {
        // 聚合出group集合
        Set<String> groupKeySet = vectorDataList.stream()
                .map(VectorData::getGroupKey)
                .collect(Collectors.toSet());

        // 从redis中查出数据
        Map<String, Map<String, double[]>> groupDataMap = listByGroup2(new ArrayList<>(groupKeySet));

        // 基于leaf进行数据删除
        vectorDataList.forEach(vectorData -> {
            String groupKey = vectorData.getGroupKey();
            String leaf = vectorData.getLeaf();
            Map<String, double[]> leafMap = groupDataMap.get(groupKey);
            if (leafMap != null) {
                leafMap.remove(leaf);
            }
        });

        // 更新回redis
        groupDataMap.forEach((groupKey, leafMap) -> {
            if (null==leafMap || leafMap.isEmpty()) {
                redis.del(groupKey);
            } else {
                String jsonData = gson.toJson(leafMap);
                redis.set(groupKey, jsonData);
            }
        });

        return true;
    }




    //根据VectorData的leaf属性列表获取向量数据列表，redis中的key是VectorData的group属性入参是List<VectorData> (class)


}
