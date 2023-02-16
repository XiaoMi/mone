package com.xiaomi.mone.app.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.xiaomi.mone.app.api.model.HeraSimpleEnv;
import com.xiaomi.mone.app.enums.OperateEnum;
import com.xiaomi.mone.app.exception.AppException;
import com.xiaomi.mone.app.model.vo.HeraAppEnvVo;
import com.xiaomi.mone.app.model.vo.HeraAppOperateVo;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import java.time.Instant;
import java.util.List;
import java.util.Random;

/**
 * @author wtt
 * @version 1.0
 * @description app和app部署后对应的环境相关的信息（这些信息可能来自于别的系统同步所致）
 * @date 2022/11/9 17:27
 */
@Data
@TableName(value = "hera_app_env", autoResultMap = true)
public class HeraAppEnv extends BaseCommon {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long heraAppId;

    private Long appId;
    /**
     * 冗余信息，防止查询时需要
     */
    private String appName;

    private Long envId;

    private String envName;

    @TableField(value = "ip_list", typeHandler = JacksonTypeHandler.class)
    private List<String> ipList;

    public HeraAppEnvVo toHeraAppEnvVo() {
        HeraAppEnvVo heraAppEnvVo = new HeraAppEnvVo();
        try {
            BeanUtils.copyProperties(this, heraAppEnvVo);
            return heraAppEnvVo;
        } catch (Exception e) {
            throw new AppException("数据转化异常", e);
        }
    }

    public HeraAppEnv operateVoToHeraAppEnv(HeraAppOperateVo operateVo, OperateEnum operateEnum) {
        if(StringUtils.isBlank(operateVo.getEnvName())){
            operateVo.setEnvName("staging");
        }
        if(null == operateVo.getEnvId()){
            operateVo.setEnvId((long) new Random().nextInt(400_0));
        }
        BeanUtils.copyProperties(operateVo, this);
        if (OperateEnum.ADD_OPERATE == operateEnum) {
            this.setCtime(Instant.now().toEpochMilli());
            this.setCreator(operateVo.getOperator());
        }
        this.setUtime(Instant.now().toEpochMilli());
        this.setUpdater(operateVo.getOperator());
        return this;
    }


    public HeraSimpleEnv toHeraSimpleEnv() {
        return HeraSimpleEnv.builder()
                .id(this.id)
                .name(this.envName)
                .ips(this.ipList)
                .build();
    }
}
