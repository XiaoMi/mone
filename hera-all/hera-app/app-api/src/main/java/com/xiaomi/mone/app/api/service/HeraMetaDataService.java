package com.xiaomi.mone.app.api.service;

import com.xiaomi.mone.app.api.model.HeraMetaDataModel;
import com.xiaomi.mone.app.api.model.HeraMetaDataQuery;

import java.util.List;

public interface HeraMetaDataService {

    int count(HeraMetaDataQuery query);

    List<HeraMetaDataModel> page(HeraMetaDataQuery query);

    int insert(HeraMetaDataModel model);

    int insert(List<HeraMetaDataModel> models);

    int delete(int metaId);
}
