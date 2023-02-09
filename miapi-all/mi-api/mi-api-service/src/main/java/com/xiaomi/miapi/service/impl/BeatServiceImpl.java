package com.xiaomi.miapi.service.impl;

import com.xiaomi.miapi.api.service.bo.BeatInfo;
import com.xiaomi.miapi.pojo.ModuleNameData;
import com.xiaomi.miapi.pojo.ModuleNameDataExample;
import com.xiaomi.miapi.mapper.ModuleNameDataMapper;
import com.xiaomi.miapi.service.BeatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author dongzhenxing
 * @date 2023/02/08
 * deal with heart beat task
 */
@Service
public class BeatServiceImpl implements BeatService {

    /**
     * thread pool for clean expire instance
     */
    private final ScheduledExecutorService scheduledPool = Executors.newSingleThreadScheduledExecutor();

    /**
     * thread pool for excuse update heart beat info
     */
    private final ExecutorService pool = Executors.newCachedThreadPool();

    private final static long expireTime = 45;

    /**
     * clean schedule time
     */
    private final static long scheduleTime = 30;


    @Autowired
    private ModuleNameDataMapper moduleMapper;

    /**
     * do clean per 30s
     */
    @PostConstruct
    public void clearExpireInstances() {
        scheduledPool.scheduleAtFixedRate(this::doClearExpireInstances, 0, scheduleTime, TimeUnit.SECONDS);
    }

    private void doClearExpireInstances() {
        ModuleNameDataExample example = new ModuleNameDataExample();
        long now = System.currentTimeMillis();
        example.createCriteria().andLastBeatTimeNotBetween(now - expireTime * 1000, now + 1000);
        moduleMapper.deleteByExample(example);
    }


    @Override
    public void beat(BeatInfo beatInfo) {
        pool.submit(() -> {
            //do up beat time
            beatInfo.getModuleNames().forEach(moduleName ->{
                ModuleNameDataExample example = new ModuleNameDataExample();
                example.createCriteria().andModuleNameEqualTo(moduleName).andAddressEqualTo(beatInfo.getAddress());
                List<ModuleNameData> moduleNameDataList = moduleMapper.selectByExample(example);
                if (moduleNameDataList != null && moduleNameDataList.size() != 0) {
                    //exist,up beat time
                    ModuleNameData instance = moduleNameDataList.get(0);
                    instance.setLastBeatTime(System.currentTimeMillis());
                    moduleMapper.updateByPrimaryKey(instance);
                }else {
                    //do not exist，register
                    ModuleNameData instance = new ModuleNameData();
                    instance.setModuleName(moduleName);
                    instance.setAddress(beatInfo.getAddress());
                    instance.setLastBeatTime(System.currentTimeMillis());
                    moduleMapper.insert(instance);
                }
            });
        });
    }
}
