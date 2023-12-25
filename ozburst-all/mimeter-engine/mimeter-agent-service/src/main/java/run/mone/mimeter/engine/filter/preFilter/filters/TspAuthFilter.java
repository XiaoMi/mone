package run.mone.mimeter.engine.filter.preFilter.filters;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.xiaomi.youpin.docean.anno.Component;
import common.Util;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import run.mone.mimeter.engine.agent.bo.data.CommonReqInfo;
import run.mone.mimeter.engine.agent.bo.data.HttpData;
import run.mone.mimeter.engine.agent.bo.data.TspAuthInfoDTO;
import run.mone.mimeter.engine.agent.bo.task.Task;
import run.mone.mimeter.engine.agent.bo.task.TaskContext;
import run.mone.mimeter.engine.agent.bo.task.TaskType;
import run.mone.mimeter.engine.filter.common.BasePreFilter;
import run.mone.mimeter.engine.filter.common.FilterOrder;
import run.mone.mimeter.engine.filter.preFilter.PreFilter;
import run.mone.mimeter.engine.filter.preFilter.PreFilterAnno;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static common.Const.*;
import static common.Const.CONTENT_TYPE_APP_FORM2;

/**
 * @author dongzhenxing
 * 汽车部 tsp接口鉴权过滤器
 */
@Component
@PreFilterAnno
@FilterOrder(1)
@Slf4j
public class TspAuthFilter extends PreFilter {

    private static final String ACCESS_KEY = "accesskey";

    private static final String SIGNATURE = "signature";

    private static final String TIMESTAMP = "timestamp";

    public static final Gson gson = Util.getGson();

    @Override
    public CommonReqInfo doFilter(Task task, TaskContext context,CommonReqInfo commonReqInfo, BasePreFilter filter) {
        HttpData httpData = task.getHttpData();
        if (task.getType() != TaskType.http || httpData == null) {
            return filter.doFilter(task, context,commonReqInfo);
        }
        //非http启用该功能的接口任务，直接返回不做处理
        TspAuthInfoDTO tspAuth = httpData.getTspAuthInfoDTO();

        if (tspAuth == null || !tspAuth.isEnableAuth()) {
            return filter.doFilter(task, context, commonReqInfo);
        }

        long now = System.currentTimeMillis();
        commonReqInfo.getHeaders().put(ACCESS_KEY, tspAuth.getAccessKey());
        commonReqInfo.getHeaders().put(SIGNATURE, genSignature(httpData, commonReqInfo, tspAuth.getAccessKey(), tspAuth.getSecretKey(), now));
        commonReqInfo.getHeaders().put(TIMESTAMP, String.valueOf(now));

        return filter.doFilter(task, context, commonReqInfo);
    }

    private String genSignature(HttpData httpData, CommonReqInfo reqInfo, String accessKey, String secretKey, long now) {
        try {
            MessageDigest sha512 = MessageDigest.getInstance("SHA-512");
            String tempStringSignature = accessKey + secretKey + now + string2Sign(httpData, reqInfo);
            byte[] tempSignature = sha512.digest(tempStringSignature.getBytes());
            return String.valueOf(Hex.encodeHex(tempSignature));
        } catch (NoSuchAlgorithmException e) {
            log.warn("TspAuthFilter genSignature failed:{}", e.getMessage());
            return "";
        }
    }

    private String string2Sign(HttpData httpData, CommonReqInfo reqInfo) {
        StringBuilder sign = new StringBuilder();
        String url = httpData.getUrl();
        String[] urlArr = url.split("//", 2);
        url = urlArr[1];
        if (httpData.getMethod().equalsIgnoreCase(HTTP_GET)) {
            //get请求
            if (!url.contains("/")) {
                sign.append("/");
            } else {
                sign.append(url.substring(url.indexOf("/")));
            }
            sign.append('+').append("GET");
            Map<String, String> paramAndValue = reqInfo.getQueryParamMap();
            List<String> keyList = paramAndValue.keySet().stream().sorted(String::compareTo).toList();
            if (keyList.size() != 0) {
                sign.append('+');
            }
            for (int i = 0; i < keyList.size(); i++) {
                sign.append(keyList.get(i).toLowerCase()).append("=").append(paramAndValue.get(keyList.get(i)));
                if (i != keyList.size() - 1) {
                    sign.append("&");
                }
            }
        } else if (httpData.getMethod().equalsIgnoreCase(HTTP_POST)) {
            //post请求
            TreeMap<String, Object> target = new TreeMap<>();
            if (!url.contains("/")) {
                sign.append("/");
            } else {
                sign.append(url.substring(url.indexOf("/")));
            }
            sign.append('+').append("POST");

            if (httpData.getContentType().equals(CONTENT_TYPE_APP_FORM) || httpData.getContentType().equals(CONTENT_TYPE_APP_FORM2)) {
                Map<String, String> paramAndValue = reqInfo.getQueryParamMap();
                //对 key 排序
                List<String> keyList = paramAndValue.keySet().stream().sorted(String::compareTo).toList();
                if (keyList.size() != 0) {
                    sign.append('+');
                }
                for (String s : keyList) {
                    target.put(s, paramAndValue.get(s));
                }
            } else {
                //json
                try {
                    target = gson.fromJson(httpData.getPostParamJson(), TreeMap.class);
                } catch (JsonSyntaxException e) {
                    log.warn("string2Sign gson.fromJson(httpData.getPostParamJson(),TreeMap.class) failed,cause :{}", e.getMessage());
                }
            }
            sign.append(gson.toJson(target));
        }
        return sign.toString();
    }
}
