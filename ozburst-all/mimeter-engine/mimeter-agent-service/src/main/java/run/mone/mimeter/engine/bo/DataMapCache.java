package run.mone.mimeter.engine.bo;

import lombok.Data;

import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

@Data
public class DataMapCache {
    private AtomicInteger countFinLinkNum;
    private TreeMap<String, List<String>> dataMap;

    public DataMapCache(AtomicInteger countFinLinkNum, TreeMap<String, List<String>> dataMap) {
        this.countFinLinkNum = countFinLinkNum;
        this.dataMap = dataMap;
    }
}
