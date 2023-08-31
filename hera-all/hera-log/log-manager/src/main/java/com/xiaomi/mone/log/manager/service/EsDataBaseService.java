/*
 * Copyright 2020 Xiaomi
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package com.xiaomi.mone.log.manager.service;

import org.elasticsearch.common.text.Text;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public interface EsDataBaseService {

    default Map<String, List<String>> getHightlinghtMap(SearchHit hit) {
        Map<String, List<String>> highlinghtMap = new HashMap<>();
        Map<String, HighlightField> highlightFields = hit.getHighlightFields();
        if (highlightFields != null && !highlightFields.isEmpty()) {
            for (Map.Entry<String, HighlightField> highlightFieldEntry : highlightFields.entrySet()) {
                List<String> highlightMessage = Arrays.stream(highlightFieldEntry.getValue().getFragments())
                        .map(Text::toString)
                        .collect(Collectors.toList());
                highlinghtMap.put(highlightFieldEntry.getKey(), highlightMessage);
            }
        }
        return highlinghtMap;
    }
}
