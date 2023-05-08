package com.xiaomi.mone.app.util;

import com.xiaomi.mone.app.api.model.HeraMetaDataMessage;
import com.xiaomi.mone.app.api.model.HeraMetaDataModel;
import com.xiaomi.mone.app.api.model.HeraMetaDataPortModel;
import com.xiaomi.mone.app.model.HeraMetaData;
import com.xiaomi.mone.app.model.HeraMetaDataPort;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @Description
 * @Author dingtao
 * @Date 2023/5/5 3:59 PM
 */
@Slf4j
public class HeraMetaDataConvertUtil {

    public static List<HeraMetaDataModel> convertToModel(List<HeraMetaData> heraMetaDataList) {
        if (heraMetaDataList == null) {
            return null;
        }
        List<HeraMetaDataModel> list = new ArrayList<>();
        for (HeraMetaData heraMetaData : heraMetaDataList) {
            try {
                HeraMetaDataPortModel portModel = null;
                if (heraMetaData.getPort() != null) {
                    portModel = new HeraMetaDataPortModel(
                            heraMetaData.getPort().getPort(),
                            heraMetaData.getPort().getDubboPort(),
                            heraMetaData.getPort().getHttpPort(),
                            heraMetaData.getPort().getGrpcPort(),
                            heraMetaData.getPort().getThriftPort());

                }
                HeraMetaDataModel model = new HeraMetaDataModel(
                        heraMetaData.getId(),
                        heraMetaData.getMetaId(),
                        heraMetaData.getMetaName(),
                        heraMetaData.getType(),
                        heraMetaData.getHost(),
                        portModel,
                        heraMetaData.getCreateTime(),
                        heraMetaData.getUpdateTime(),
                        heraMetaData.getCreateBy(),
                        heraMetaData.getUpdateBy());
                list.add(model);
            } catch (Exception e) {
                log.error("bean convert error : ", e);
            }
        }
        return list;
    }

    public static HeraMetaDataModel convertToModel(HeraMetaData heraMetaData){
        List<HeraMetaDataModel> heraMetaDataModels = convertToModel(Collections.singletonList(heraMetaData));
        if(heraMetaDataModels != null && heraMetaDataModels.size() > 0){
            return heraMetaDataModels.get(0);
        }
        return null;
    }

    public static List<HeraMetaData> modelConvertTo(List<HeraMetaDataModel> heraMetaDataList) {
        if (heraMetaDataList == null) {
            return null;
        }
        List<HeraMetaData> list = new ArrayList<>();
        for (HeraMetaDataModel model : heraMetaDataList) {
            try {
                HeraMetaDataPort portModel = null;
                if (model.getPort() != null) {
                    portModel = new HeraMetaDataPort(
                            model.getPort().getPort(),
                            model.getPort().getDubboPort(),
                            model.getPort().getHttpPort(),
                            model.getPort().getGrpcPort(),
                            model.getPort().getThriftPort());
                }
                HeraMetaData data = new HeraMetaData(
                        model.getId(),
                        model.getMetaId(),
                        model.getMetaName(),
                        model.getType(),
                        model.getHost(),
                        portModel,
                        model.getCreateTime(),
                        model.getUpdateTime(),
                        model.getCreateBy(),
                        model.getUpdateBy());
                list.add(data);
            } catch (Exception e) {
                log.error("bean convert error : ", e);
            }
        }
        return list;
    }

    public static HeraMetaData modelConvertTo(HeraMetaDataModel model){
        List<HeraMetaData> heraMetaData = modelConvertTo(Collections.singletonList(model));
        if(heraMetaData != null && heraMetaData.size() > 0){
            return heraMetaData.get(0);
        }
        return null;
    }

    public static HeraMetaDataPort modelPortConvertTo(HeraMetaDataPortModel portModel){
        HeraMetaDataPort port = new HeraMetaDataPort(
                portModel.getPort(),
                portModel.getDubboPort(),
                portModel.getHttpPort(),
                portModel.getGrpcPort(),
                portModel.getThriftPort());
        return port;
    }

    public static HeraMetaData messageConvertTo(HeraMetaDataMessage heraMetaDataMessage){
        HeraMetaDataPort heraMetaDataPort = modelPortConvertTo(heraMetaDataMessage.getPort());
        HeraMetaData heraMetaData = new HeraMetaData(
                null,
                heraMetaDataMessage.getMetaId(),
                heraMetaDataMessage.getMetaName(),
                heraMetaDataMessage.getType(),
                heraMetaDataMessage.getHost(),
                heraMetaDataPort,
                heraMetaDataMessage.getCreateTime(),
                heraMetaDataMessage.getUpdateTime(),
                heraMetaDataMessage.getCreateBy(),
                heraMetaDataMessage.getUpdateBy());
        return heraMetaData;
    }
}
