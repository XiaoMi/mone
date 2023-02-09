package com.xiaomi.youpin.gwdash.service;

import com.xiaomi.youpin.gwdash.bo.MetaDataParam;
import com.xiaomi.youpin.gwdash.common.Result;
import com.xiaomi.youpin.gwdash.dao.model.MetaData;
import com.xiaomi.youpin.gwdash.exception.CommonError;
import com.xiaomi.youpin.gwdash.service.impl.TenantComponent;
import org.apache.commons.lang3.StringUtils;
import org.nutz.dao.Chain;
import org.nutz.dao.Cnd;
import org.nutz.dao.Dao;
import org.nutz.dao.pager.Pager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MetaDataService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MetaDataService.class);

    @Autowired
    private Dao dao;

    @Autowired
    private TenantComponent tenementComponent;

    public Result<Void> newMetadata(MetaDataParam param, int type) {
        if (null == param || StringUtils.isBlank(param.getName()) || StringUtils.isBlank(param.getDescription())) {
            return Result.fail(CommonError.InvalidParamError);
        }
        if (param.getReferHeader() == null) {
            param.setReferHeader("");
        }
        MetaData md = new MetaData();
        md.setName(param.getName());
        md.setDescription(param.getDescription());
        md.setReferHeader(param.getReferHeader());
        md.setType(type);
        long now = System.currentTimeMillis();
        md.setCtime(now);
        md.setUtime(now);
        md.setTenement(tenementComponent.getTenement());
        dao.insert(md);
        return new Result<>(CommonError.Success.getCode(), CommonError.Success.getMessage());
    }

    public Result<Void> updateMetaData(MetaDataParam param) {
        if (null == param || param.getId() <= 0 || StringUtils.isBlank(param.getName()) || StringUtils.isBlank(param.getDescription())) {
            return Result.fail(CommonError.InvalidParamError);
        }
        if (param.getReferHeader() == null) {
            param.setReferHeader("");
        }
        long now = System.currentTimeMillis();
        int row = dao.update(MetaData.class, Chain.make("name", param.getName()).add("description", param.getDescription()).add("refer_header", param.getReferHeader()).add("utime", now), Cnd.where("id", "=", param.getId()));
        if (row != 1) {
            return Result.fail(CommonError.UnknownError);
        }
        return new Result<>(CommonError.Success.getCode(), CommonError.Success.getMessage());
    }

    @Transactional("masterTransactionManager")
    public Result<Void> deleteMetaData(Integer id) {
        if (id <= 0) {
            return Result.fail(CommonError.InvalidParamError);
        }
        int delete = dao.delete(MetaData.class, id);
        if (delete < 1) {
            return new Result<>(CommonError.UnknownError.getCode(), "记录不存在");
        }
        dao.clear("metadata_relation", Cnd.where("source", "=", id));
        return new Result<>(CommonError.Success.getCode(), CommonError.Success.getMessage());
    }

    public Map<String, Object> getMetaDataList(String name, int page, int pagesize, int type) {
        Cnd cnd = Cnd.where("type", "=", type);
        if (name != null) {
            cnd = cnd.and("name", "like", "%" + name + "%");
        }

        cnd.and("tenant", "=", tenementComponent.getTenement());
        List<MetaData> apiGroupClusters = dao.query(MetaData.class, cnd, new Pager(page, pagesize));

        Map<String, Object> result = new HashMap<>();
        result.put("metaDataList", apiGroupClusters);
        result.put("total", dao.count("metadata",
                Cnd.where("type", "=", type).and("tenant", "=", tenementComponent.getTenement())
        ));
        result.put("page", page);
        result.put("pageSize", pagesize);
        return result;
    }

    public Map<String, Object> getMetaDataListAll(String name, int type) {
        Cnd cnd = Cnd.where("type", "=", type);
        if (name != null) {
            cnd = cnd.and("name", "like", "%" + name + "%");
        }
        cnd.and("tenant", "=", tenementComponent.getTenement());
        List<MetaData> apiGroupClusters = dao.query(MetaData.class, cnd);
        Map<String, Object> result = new HashMap<>();
        result.put("metaDataList", apiGroupClusters);
        result.put("total", dao.count("metadata", Cnd.where("type", "=", type)));
        return result;
    }

    public List<MetaData> getMetaDataList(int type) {
        Cnd cnd = Cnd.where("type", "=", type).and("tenant", "=", tenementComponent.getTenement());
        List<MetaData> apiGroupClusters = dao.query(MetaData.class, cnd);
        return apiGroupClusters;
    }

    public List<MetaData> getMetaDataList(int type, String tenant) {
        Cnd cnd = Cnd.where("type", "=", type);
        if (StringUtils.isNotEmpty(tenant)) {
            cnd.and("tenant", "=", tenant);
        }
        List<MetaData> apiGroupClusters = dao.query(MetaData.class, cnd);
        return apiGroupClusters;
    }

    public List<MetaData> getMetaDataList(List<Integer> list, int type) {
        return dao.query(MetaData.class, Cnd.where("id", "in", list).and("type", "=", type));
    }

    public MetaData getMetaDataById(int id) {
        return dao.fetch(MetaData.class, id);
    }

    public boolean verifyExistByName(String name, int type) {
        int count = dao.count("metadata", Cnd.where("name", "=", name).and("type", "=", type).and("tenant", "=", tenementComponent.getTenement()));
        return count > 0;
    }
}
