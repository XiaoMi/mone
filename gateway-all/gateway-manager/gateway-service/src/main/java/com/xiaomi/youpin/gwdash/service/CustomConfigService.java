package com.xiaomi.youpin.gwdash.service;

import com.xiaomi.youpin.gwdash.dao.model.TCustomeConfig;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.pager.Pager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author tsingfu
 *
 */
@Service
public class CustomConfigService {

    @Autowired
    private Dao dao;

    public Map<String, Object> list(int page, int pageSize) {
        Map<String, Object> map = new HashMap<>();
        map.put("total", dao.count(TCustomeConfig.class));
        map.put("list", dao.query(TCustomeConfig.class, null, new Pager(page, pageSize)));
        return map;
    }

    public Boolean create(TCustomeConfig tCustomeConfigBo) {
        int count = dao.count(TCustomeConfig.class, Cnd.where("name", "=", tCustomeConfigBo.getName()));
        if (count <= 0) {
            long now = System.currentTimeMillis();
            tCustomeConfigBo.setCtime(now);
            tCustomeConfigBo.setUtime(now);
            dao.insert(tCustomeConfigBo);
            return true;
        }
        return false;
    }

    public Boolean update(TCustomeConfig tCustomeConfigBo) {
        TCustomeConfig tCustomeConfig = dao.fetch(tCustomeConfigBo);
        if (null != tCustomeConfig) {
            long now = System.currentTimeMillis();
            tCustomeConfig.setContent(tCustomeConfigBo.getContent());
            tCustomeConfig.setUtime(now);
            dao.update(tCustomeConfig);
            return true;
        }
        return false;
    }

    public Integer delete(long id) {
        return dao.delete(TCustomeConfig.class, id);
    }

    public TCustomeConfig get(String key) {
        return dao.fetch(TCustomeConfig.class, Cnd.where("name", "=", key));
    }
}
