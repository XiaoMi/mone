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

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;
import org.springframework.stereotype.Service;
import run.mone.knowledge.api.dto.VectorData;
import run.mone.knowledge.api.dto.VectorLimits;
import run.mone.knowledge.service.OzVectorDbService;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author shanwb
 * @date 2024-02-06
 */
@Service
@Slf4j
public class OzVectorDbServiceImpl implements OzVectorDbService {

    @Override
    public List<VectorData> cosineSimilarity(double[] queryVector, List<VectorData> vectorDataList, VectorLimits limits) {
        List<VectorData> topVectorList = vectorDataList.parallelStream().map(v -> {
            double similarity = cosineSimilarity(queryVector, v.getVector());
            v.setSimilarity(similarity);
            return v;
        }).filter(v -> {
            if (null != limits.getSimilarity()) {
                return v.getSimilarity() > limits.getSimilarity();
            }
            return true;
        }).sorted(Comparator.comparingDouble(VectorData::getSimilarity).reversed())
        .limit(null == limits.getRealTopN() ? Long.MAX_VALUE : limits.getRealTopN())
        .collect(Collectors.toList());

        return topVectorList;
    }

    public static double cosineSimilarity(double[] vectorA, double[] vectorB) {
        RealVector a = new ArrayRealVector(vectorA);
        RealVector b = new ArrayRealVector(vectorB);
        return a.cosine(b);
    }

}
