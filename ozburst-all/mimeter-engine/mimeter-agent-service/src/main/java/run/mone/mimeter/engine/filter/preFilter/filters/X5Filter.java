package run.mone.mimeter.engine.filter.preFilter.filters;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.xiaomi.data.push.common.SafeRun;
import com.xiaomi.youpin.docean.anno.Component;
import common.Util;
import org.apache.commons.codec.Charsets;
import run.mone.mimeter.dashboard.bo.sceneapi.X5VersionEnum;
import run.mone.mimeter.engine.agent.bo.data.ApiX5InfoDTO;
import run.mone.mimeter.engine.agent.bo.data.CommonReqInfo;
import run.mone.mimeter.engine.agent.bo.data.HttpData;
import run.mone.mimeter.engine.agent.bo.task.Task;
import run.mone.mimeter.engine.agent.bo.task.TaskContext;
import run.mone.mimeter.engine.agent.bo.task.TaskType;
import run.mone.mimeter.engine.common.Safe;
import run.mone.mimeter.engine.filter.common.BasePreFilter;
import run.mone.mimeter.engine.filter.common.FilterOrder;
import run.mone.mimeter.engine.filter.preFilter.PreFilter;
import run.mone.mimeter.engine.filter.preFilter.PreFilterAnno;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author dongzhenxing
 * x5 鉴权过滤器
 */
@Component
@FilterOrder(2)
@PreFilterAnno
public class X5Filter extends PreFilter {

    private static final Gson gson = Util.getGson();

    @Override
    public CommonReqInfo doFilter(Task task, TaskContext context, CommonReqInfo commonReqInfo, BasePreFilter filter) {

        //非http任务，直接略过
        if (task.getType() != TaskType.http) {
            return filter.doFilter(task,context, commonReqInfo);
        }
        HttpData httpData = task.getHttpData();
        //未开启x5
        if (httpData == null || httpData.getApiX5InfoDTO() == null || !httpData.getApiX5InfoDTO().isEnableX5()){
            return filter.doFilter(task,context,commonReqInfo);
        }
        //启用x5协议
        commonReqInfo.setParamJson(calculateAndReplaceX5Params(httpData.getApiX5InfoDTO(),commonReqInfo));
        return filter.doFilter(task, context,commonReqInfo);
    }

    private String calculateAndReplaceX5Params(ApiX5InfoDTO x5InfoDTO,CommonReqInfo commonReqInfo){
        String x5Version = x5InfoDTO.getX5Version();
        Map<String, String> body = new HashMap<>();
        SafeRun.run(() ->{
            Map<String, Object> x5Body = new HashMap<>();
            Map<String, String> x5Header = new HashMap<>();
            x5Header.put("appid", x5InfoDTO.getAppId());
            if (!Strings.isNullOrEmpty(x5InfoDTO.getX5Method())) {
                x5Header.put("method", x5InfoDTO.getX5Method());
            }
            String sign = genSign(x5InfoDTO.getAppId(), commonReqInfo.getParamJson(), x5InfoDTO.getAppKey());
            x5Header.put("sign", sign);
            x5Body.put("header", x5Header);
            x5Body.put("body", commonReqInfo.getParamJson());
            String data = gson.toJson(x5Body);
            body.put("data", Base64.getEncoder().encodeToString(data.getBytes(StandardCharsets.UTF_8)));
        });

        if (X5VersionEnum.CHINA_VERSION.version.equals(x5Version)) {
            return gson.toJson(body);
        } else if (X5VersionEnum.INFORMATION_VERISON.version.equals(x5Version)) {
            return "\""+body.get("data")+"\""; //todo 临时修改，改造json进制转换==
        }
        return gson.toJson(body);
    }


    /**
     * @param appId 应用id
     * @param body   入参
     * @param appKey 密钥 secret
     * @return 签名
     */
    private static String genSign(String appId, String body, String appKey) {
        String str = appId + body + appKey;
        StringBuilder md5CodeBuffer = new StringBuilder();
        try {
            MessageDigest md5Digest = MessageDigest.getInstance("MD5");
            md5Digest.update(str.getBytes());
            byte[] strBytesDigest = md5Digest.digest();

            // 将二进制字节信息转换为十六进制字符串
            String strHexDigest;
            for (byte b : strBytesDigest) {
                strHexDigest = Integer.toHexString(b & 0XFF);
                if (strHexDigest.length() == 1) {
                    md5CodeBuffer.append("0").append(strHexDigest);
                } else {
                    md5CodeBuffer.append(strHexDigest);
                }
            }
        } catch (Exception e) {
            return str;
        }

        return md5CodeBuffer.toString().toUpperCase();
    }
}
