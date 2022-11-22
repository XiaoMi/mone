package com.xiaomi.youpin.gwdash.service;

import com.xiaomi.youpin.gwdash.bo.MetaDataParam;
import com.xiaomi.youpin.gwdash.common.Result;
import com.xiaomi.youpin.gwdash.dao.model.MetaData;
import com.xiaomi.youpin.gwdash.dao.model.MetaDataRelation;
import com.xiaomi.youpin.gwdash.exception.CommonError;
import org.apache.commons.lang3.StringUtils;
import org.nutz.dao.*;
import org.nutz.dao.entity.Record;
import org.nutz.dao.pager.Pager;
import org.nutz.dao.sql.Sql;
import org.nutz.dao.sql.SqlCallback;
import org.nutz.dao.util.Daos;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Service
public class MetaDataRelationService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MetaDataRelationService.class);

    @Autowired
    private Dao dao;

   public List<MetaDataRelation> getMetaDataRelationBySource(int source,int type){
      return dao.query(MetaDataRelation.class, Cnd.where("source", "=", source).and("type", "=", type));
   }

   public int getMetaDataRelationCountsById(int source,int type){
       return dao.count("metadata_relation", Cnd.where("source", "=", source).and("type", "=", type));
   }

   public List<MetaDataRelation> getMetaDataRelationBySourceWith2Type(int source,int type1,int type2){
       return dao.query(MetaDataRelation.class, Cnd.where("source", "=", source).and("type", "=", type1).or("type","=",type2));
   }

   public List<MetaDataRelation> getMetaDataRelationByTargetList(List<Integer> target,int type){
       return dao.query(MetaDataRelation.class,Cnd.where("target","in",target).and("type","=",type));
   }

   public List<MetaDataRelation> getMetaDataRelationBySourceList(List<Integer> source,int type){
       return dao.query(MetaDataRelation.class, Cnd.where("source", "in", source).and("type", "=", type));
   }

    public List<Integer> getTargetListBySource(List<Integer> source,int type){
        Sql sql = Sqls.create("select target from metadata_relation $condition");

        sql.setCondition(Cnd.where("source","in",source).and("type","=",type));

        sql.setCallback(new SqlCallback() {
            @Override
            public Object invoke(Connection conn, ResultSet rs, Sql sql) throws SQLException {
                List<Integer> list = new ArrayList<>();
                while (rs.next()){
                    int target = rs.getInt("target");
                    list.add(target);
                }
                return list;
            }
        });
        dao.execute(sql);
        return sql.getList(Integer.class);
    }

    public List<Integer> getSourceListByTarget(List<Integer> target,int type){
        Sql sql = Sqls.create("select source from metadata_relation $condition");

        sql.setCondition(Cnd.where("target","in",target).and("type","=",type));

        sql.setCallback(new SqlCallback() {
            @Override
            public Object invoke(Connection conn, ResultSet rs, Sql sql) throws SQLException {
                List<Integer> list = new ArrayList<>();
                while (rs.next()){
                    int target = rs.getInt("source");
                    list.add(target);
                }
                return list;
            }
        });
        dao.execute(sql);
        return sql.getList(Integer.class);
    }
}
