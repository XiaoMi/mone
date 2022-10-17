package com.xiaomi.mone.http.docs.util;


import com.xiaomi.mone.http.docs.core.beans.HttpApiCacheItem;
import com.xiaomi.mone.http.docs.core.beans.HttpLayerItem;

import java.util.Objects;


public class AdapteUtil {
    public HttpApiCacheItem clearUnSupportType(HttpApiCacheItem apiCacheItem) {
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

    private void recursiveClearLayerItem(HttpLayerItem layerItem) {
        if (Objects.nonNull(layerItem.getItemClass())) {
            layerItem.setItemClass(null);
        }
        if (Objects.nonNull(layerItem.getItemType())) {
            layerItem.setItemType(null);
        }
        if (Objects.nonNull(layerItem.getChildList()) && !layerItem.getChildList().isEmpty()) {
            layerItem.getChildList().forEach(layerItem1 -> {
                if (Objects.nonNull(layerItem1)) {
                    recursiveClearLayerItem(layerItem1);
                }
            });
        }
    }
}
