package run.mone.mimeter.dashboard.bo.sceneapi;

import com.xiaomi.mone.http.docs.annotations.HttpApiDocClassDefine;
import lombok.Data;

import java.io.Serializable;

@Data
public class SceneApiOutputParam implements Serializable {
    @HttpApiDocClassDefine(value = "paramName", required = true, description = "参数名称", defaultValue = "result")
    private String paramName;

    @HttpApiDocClassDefine(value = "origin", required = true, description = "参数来源 1：Body:json 2:Body:txt", defaultValue = "1")
    private Integer origin;

    @HttpApiDocClassDefine(value = "originParseExpr", required = true, description = "原解析表达式", defaultValue = "")
    private String originParseExpr;

    @HttpApiDocClassDefine(value = "parseExpr", required = true, description = "解析表达式", defaultValue = "")
    private String parseExpr;
}
