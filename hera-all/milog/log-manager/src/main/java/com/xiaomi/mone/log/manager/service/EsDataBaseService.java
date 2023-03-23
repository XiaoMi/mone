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
