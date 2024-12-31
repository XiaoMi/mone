package run.mone.mimeter.engine.common;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static common.Const.API_TYPE_HTTP;

public class CustomLogBuilder {

    public static DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String buildApiLog(boolean failed, int taskId, int apiType,String url, String method,
                                     long elapsed, int code, Integer sceneId, Integer serialId, String reportId,
                                     Integer apiId, String resContent, String paramBody, String reqHeader,
                                     String resHeader, String traceId, String errorInfo) {
        return sdf.format(new Date()) + "∩" +
                failed + "∩" +
                taskId + "∩" +
                apiType + "∩" +
                url + "∩" +
                method + "∩" +
                elapsed + "∩" +
                code + "∩" +
                sceneId + "∩" +
                serialId + "∩" +
                reportId + "∩" +
                apiId + "∩" +
                resContent + "∩" +
                paramBody + "∩" +
                reqHeader + "∩" +
                resHeader + "∩" +
                traceId + "∩" +
                errorInfo;
    }

}
