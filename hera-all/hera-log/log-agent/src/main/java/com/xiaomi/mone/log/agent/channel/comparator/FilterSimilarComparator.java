package com.xiaomi.mone.log.agent.channel.comparator;

import com.xiaomi.mone.log.api.model.meta.FilterConf;

import java.util.List;

public class FilterSimilarComparator implements SimilarComparator<List<FilterConf>> {
    private List<FilterConf> filterConf;

    public FilterSimilarComparator(List<FilterConf> confs) {
        this.filterConf = confs;
    }

    @Override
    public boolean compare(List<FilterConf> confs) {
        if (confs == null && filterConf == null) {
            return true;
        } else if (confs != null && filterConf != null) {
            if (confs.size() != filterConf.size()) {
                return false;
            }
            return confs.equals(filterConf);
        }
        return false;
    }
}
