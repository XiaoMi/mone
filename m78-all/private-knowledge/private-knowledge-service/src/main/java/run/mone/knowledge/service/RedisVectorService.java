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

package run.mone.knowledge.service;

import run.mone.knowledge.api.dto.VectorData;

import java.util.List;
import java.util.Map;

/**
 * @author shanwb
 * @date 2024-02-06
 */
public interface RedisVectorService {
    String SPLIT_CHAR = "_#_";

    /**
     * 存储向量化结果，注意会按group进行全量覆盖，确保传递的是group下的全量数据
     *
     * @param vectorDataList
     * @return
     */
    boolean storeVectorByGroup(List<VectorData> vectorDataList);

    /**
     * 按叶子节点进行向量更新
     *
     * @param vectorDataList
     * @return
     */
    boolean updateVectorByLeaf(List<VectorData> vectorDataList);

    /**
     * 按group查询所有向量结果
     *
     * @param groupKeyList
     * @return
     */
    List<VectorData> listByGroup(List<String> groupKeyList);


    /**
     * 生成一个映射，其键是字符串，值是另一个映射，该映射的键是字符串，值是双精度数组，根据提供的分组列表进行组织。
     */
    Map<String, Map<String, double[]>> listByGroup2(List<String> groupKeyList);

    boolean deleteByGroup(List<String> groupKeyList);

    boolean deleteByLeaf(List<VectorData> vectorDataList);




}
