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

package run.mone.knowledge.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;

import java.io.Serializable;

/**
 * @author shanwb
 * @date 2024-02-06
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VectorData implements Serializable {

    public static String SPLIT_CHAR = "_#_";

    private String type;

    /**
     * 向量元数据倒数第二级节点，比如：文件id、模块id等
     */
    private String group;

    /**
     * 向量元数据叶子节点,比如文本块id、类id等
     */
    private String leaf;

    private double[] vector;

    private Double similarity;

    public String getGroupKey() {
        return makeGroupKey(type, group);
    }

    public static String makeGroupKey(String type, String group) {
        return type + SPLIT_CHAR + group;
    }

    public static Pair<String, String> parseGroupKey(String groupKey) {
        String[] arr = groupKey.split(SPLIT_CHAR);
        return Pair.of(arr[0], arr[1]);
    }

}
