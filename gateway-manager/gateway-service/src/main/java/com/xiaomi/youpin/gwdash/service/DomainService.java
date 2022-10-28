package com.xiaomi.youpin.gwdash.service;

import com.xiaomi.youpin.gwdash.bo.MetaDataParam;
import com.xiaomi.youpin.gwdash.common.MetaDataRelationTypeEnum;
import com.xiaomi.youpin.gwdash.common.MetaDataTypeEnum;
import com.xiaomi.youpin.gwdash.common.Result;
import com.xiaomi.youpin.gwdash.dao.model.MetaData;
import com.xiaomi.youpin.gwdash.exception.CommonError;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class DomainService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DomainService.class);

    @Autowired
    private Dao dao;

    @Autowired
    private MetaDataService metaDataService;

    public Result<Void> newDomain(MetaDataParam param){
        if (metaDataService.verifyExistByName(param.getName(),MetaDataTypeEnum.Domain.getType())){
            return new Result<>(CommonError.UnknownError.getCode(), "存在相同Name");
        }
        return metaDataService.newMetadata(param,MetaDataTypeEnum.Domain.getType());
    }

    public Result<Void> updateDomain(MetaDataParam param){
        MetaData metaData = metaDataService.getMetaDataById(param.getId());
        if (metaData == null){
            return new Result<>(CommonError.UnknownError.getCode(), "记录不存在");
        }
        if (metaData.getType()!=MetaDataTypeEnum.Domain.getType()){
            return new Result<>(CommonError.UnknownError.getCode(), "参数异常");
        }
        return metaDataService.updateMetaData(param);
    }

    public Result<Void> deleteDomain(Integer id){
        MetaData metaData = metaDataService.getMetaDataById(id);
        if (metaData == null){
            return new Result<>(CommonError.UnknownError.getCode(), "记录不存在");
        }
        if (metaData.getType()!=MetaDataTypeEnum.Domain.getType()){
            return new Result<>(CommonError.UnknownError.getCode(), "参数异常");
        }
        // 如果存在domain被使用情况，则禁止删除
        int count = dao.count("metadata_relation", Cnd.where("target", "=", id).and("type", "=", MetaDataRelationTypeEnum.ApiGroupCluster2Domain.getType()));
        if (count>0){
            return new Result<>(CommonError.UnknownError.getCode(), "该域名已被使用，禁止删除");
        }
        int clear = dao.clear(MetaData.class, Cnd.where("id", "=", id).and("type", "=",MetaDataTypeEnum.Domain.getType()));
        if (clear<1){
            return new Result<>(CommonError.UnknownError.getCode(), "删除失败");
        }
        return new Result<>(CommonError.Success.getCode(), CommonError.Success.getMessage());
    }

    public Map<String,Object> getDomainList(String name,int page,int pagesize){
       return metaDataService.getMetaDataList(name,page,pagesize,MetaDataTypeEnum.Domain.getType());
    }

    public Map<String,Object> getDomainListAll(String name){
        return metaDataService.getMetaDataListAll(name,MetaDataTypeEnum.Domain.getType());
    }

    public Map<String,Object> getMetaDataByName(int page,int pageSize){
        return null;
    }
}
