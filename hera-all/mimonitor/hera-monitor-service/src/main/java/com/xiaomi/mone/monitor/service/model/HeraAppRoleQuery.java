package com.xiaomi.mone.monitor.service.model;

import com.xiaomi.mone.app.api.model.HeraAppRoleModel;
import lombok.Data;
import lombok.ToString;
import org.springframework.beans.BeanUtils;

/**
 * @author gaoxihui
 * @date 2022/11/23 8:08 下午
 */
@Data
@ToString
public class HeraAppRoleQuery {

    private Integer id;

    private String appId;

    private Integer appPlatform;

    private String user;

    private Integer role;

    private Integer page;

    private Integer pageSize;

    public HeraAppRoleModel getModel(){
        HeraAppRoleModel model = new HeraAppRoleModel();
        BeanUtils.copyProperties(this,model);
        return model;
    }
}
