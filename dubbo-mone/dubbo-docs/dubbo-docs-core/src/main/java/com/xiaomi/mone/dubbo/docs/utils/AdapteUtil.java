package com.xiaomi.mone.dubbo.docs.utils;

import com.xiaomi.mone.dubbo.docs.core.beans.ApiCacheItem;
import com.xiaomi.mone.dubbo.docs.core.beans.LayerItem;

import java.util.Objects;


public class AdapteUtil {
    public ApiCacheItem clearUnSupportType(ApiCacheItem apiCacheItem) {
        if (Objects.nonNull(apiCacheItem.getResponseLayer())) {
            recursiveClearLayerItem(apiCacheItem.getResponseLayer());
        }
        if (Objects.nonNull(apiCacheItem.getParamsLayerList()) && !apiCacheItem.getParamsLayerList().isEmpty()) {
            apiCacheItem.getParamsLayerList().forEach(layerItem -> {
                        if (Objects.nonNull(layerItem)) {
                            recursiveClearLayerItem(layerItem);
                        }
                    }
            );
        }
        return apiCacheItem;
    }

    private void recursiveClearLayerItem(LayerItem layerItem) {
        if (Objects.nonNull(layerItem.getItemClass())) {
            layerItem.setItemClass(null);
        }
        if (Objects.nonNull(layerItem.getItemType())) {
            layerItem.setItemType(null);
        }
        if (Objects.nonNull(layerItem.getItemValue()) && !layerItem.getItemValue().isEmpty()) {
            layerItem.getItemValue().forEach(layerItem1 -> {
                if (Objects.nonNull(layerItem1)) {
                    recursiveClearLayerItem(layerItem1);
                }
            });
        }
    }
}
