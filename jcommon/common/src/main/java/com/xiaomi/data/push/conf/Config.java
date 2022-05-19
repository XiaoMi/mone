package com.xiaomi.data.push.conf;


//import com.xiaomi.data.push.annotation.Cache;
import com.xiaomi.data.push.dao.mapper.ConfMapper;
import com.xiaomi.data.push.dao.model.Conf;
import com.xiaomi.data.push.dao.model.ConfExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author goodjava@qq.com
 */
@Component
public class Config {

    @Autowired
    private ConfMapper confMapper;

    /**
     * 根据key 获取 value 配置
     *
     * @param key
     * @return
     */
//    @Cache(paramIndex = {"0"})
    public String getValue(String key,String defaultValue) {
        ConfExample example = new ConfExample();
        example.createCriteria().andConfKeyEqualTo(key);
        List<Conf> list = confMapper.selectByExampleWithBLOBs(example);
        if (list.size() > 0) {
            return list.get(0).getConfValue();
        }
        return defaultValue;
    }

}
