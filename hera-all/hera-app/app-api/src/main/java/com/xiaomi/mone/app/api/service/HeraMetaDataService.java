package com.xiaomi.mone.app.api.service;

import com.xiaomi.mone.app.api.model.HeraMetaDataModel;

import java.util.List;

public interface HeraMetaDataService {
    List<HeraMetaDataModel> getAll();

    int insert(HeraMetaDataModel model);

    int insert(List<HeraMetaDataModel> models);

    int delete(int metaId);
}
