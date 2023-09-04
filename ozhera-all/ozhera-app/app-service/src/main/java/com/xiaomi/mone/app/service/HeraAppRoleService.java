package com.xiaomi.mone.app.service;

import com.xiaomi.mone.app.api.model.HeraAppRoleModel;
import com.xiaomi.mone.app.dao.HeraAppRoleDao;
import com.xiaomi.mone.app.model.HeraAppRole;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author gaoxihui
 * @date 2022/11/22 7:49 下午
 */
@Slf4j
@Service
public class HeraAppRoleService {

    @Autowired
    HeraAppRoleDao dao;

    public Integer delById(Integer id){
        return dao.delById(id);
    }

    public Integer addRoleGet(String appId, Integer plat, String user){
        HeraAppRoleModel role = new HeraAppRoleModel();
        role.setAppId(appId);
        role.setAppPlatform(plat);
        role.setUser(user);
        role.setStatus(1);
        role.setRole(0);
        role.setCreateTime(new Date());
        role.setUpdateTime(new Date());
        return addRole(role);
    }

    public Integer addRole(HeraAppRoleModel roleModel){

        HeraAppRole role = new HeraAppRole();
        BeanUtils.copyProperties(roleModel,role);
        return dao.create(role);
    }

    public List<HeraAppRoleModel> query(HeraAppRoleModel roleModel,Integer pageCount,Integer pageNum){

        HeraAppRole role = new HeraAppRole();
        BeanUtils.copyProperties(roleModel,role);
        List<HeraAppRole> query = dao.query(role, pageCount, pageNum);

        List<HeraAppRoleModel> result = new ArrayList<>();
        if(!CollectionUtils.isEmpty(query)){
            query.stream().forEach(t ->{
                HeraAppRoleModel model = new HeraAppRoleModel();
                BeanUtils.copyProperties(t,model);
                result.add(model);
            });
        }

        return result;

    }

    public Long count(HeraAppRoleModel roleModel){
        HeraAppRole role = new HeraAppRole();
        BeanUtils.copyProperties(roleModel,role);
        Long count = dao.count(role);
        return count;
    }

}
