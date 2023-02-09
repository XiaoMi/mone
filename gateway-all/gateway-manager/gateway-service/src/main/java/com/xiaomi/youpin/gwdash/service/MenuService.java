package com.xiaomi.youpin.gwdash.service;

import com.xiaomi.youpin.gwdash.dao.model.Menu;
import lombok.extern.slf4j.Slf4j;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.pager.Pager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class MenuService {

    @Autowired
    private Dao dao;

//    public Map<String, Object> list(int page, int pageSize) {
//        Map<String, Object> map = new HashMap<>();
//        map.put("total", dao.count(Menu.class));
//        map.put("list", dao.query(Menu.class, null, new Pager(page, pageSize)));
//        return map;
//    }
//
//    public Boolean create(Menu menu) {
//        long now = System.currentTimeMillis();
//        menu.setCtime(now);
//        menu.setUtime(now);
//        dao.insert(menu);
//        return true;
//    }
//
//    public Boolean update(Menu menu) {
//        long now = System.currentTimeMillis();
//        menu.setUtime(now);
//        dao.update(menu);
//        return true;
//    }
//
//    public Boolean delete(long id) {
//        dao.delete(Menu.class, id);
//        return true;
//    }

    public Menu menu(List<String> roleNames) {
        return dao.fetch(Menu.class, Cnd.where("role", "in", roleNames).desc("priority"));
    }
}
