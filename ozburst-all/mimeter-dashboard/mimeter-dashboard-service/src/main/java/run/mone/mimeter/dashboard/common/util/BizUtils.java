package run.mone.mimeter.dashboard.common.util;

import run.mone.mimeter.dashboard.bo.sceneapi.OutputOriginEnum;
import run.mone.mimeter.dashboard.bo.sceneapi.SceneApiOutputParam;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static run.mone.mimeter.dashboard.bo.common.Constants.*;

public final class BizUtils {
    private BizUtils() {
    }

    private static final Pattern EL_PATTERN = Pattern.compile("\\{([^}]*)}");

    /**
     * 处理转换出参定义表达式
     *
     * @param outputParams
     */
    public static void processOutputParamExpr(List<SceneApiOutputParam> outputParams) {
        for (SceneApiOutputParam outputParam :
                outputParams) {
            if (outputParam.getOrigin() == OutputOriginEnum.BODY_TXT.code) {
                continue;
            }
            //已转换过表达式
            if (outputParam.getParseExpr().contains(DEFAULT_EXPR_PREX) || outputParam.getParseExpr().contains(DEFAULT_EXPR_JSON_PREX)){
                continue;
            }
            if (outputParam.getOriginParseExpr() == null) {
                outputParam.setOriginParseExpr(outputParam.getParseExpr());
            }

            if (outputParam.getParseExpr().contains(EXPR_INT_FLAG)) {
                //值为整型
                outputParam.setOriginParseExpr(outputParam.getParseExpr());

                StringBuilder realExpr = new StringBuilder(DEFAULT_EXPR_JSON_PREX);
                Matcher m = EL_PATTERN.matcher(outputParam.getParseExpr());

                while (m.find()) {
                    String innerKey = m.group(1);
                    if (innerKey.contains(EXPR_INT_FLAG)) {
                        //目标字段
                        String k = innerKey.substring(0, innerKey.indexOf(":"));
                        realExpr.append(".get(").append(k).append(").getAsInt()");
                    } else {
                        realExpr.append(".get(").append(innerKey).append(")");
                    }
                }
                outputParam.setParseExpr(realExpr.toString());
            } else if (outputParam.getParseExpr().contains(EXPR_STRING_FLAG)) {
                //转为字符串
                outputParam.setOriginParseExpr(outputParam.getParseExpr());

                StringBuilder realExpr = new StringBuilder(DEFAULT_EXPR_JSON_PREX);
                Matcher m = EL_PATTERN.matcher(outputParam.getParseExpr());

                while (m.find()) {
                    String innerKey = m.group(1);
                    if (innerKey.contains(EXPR_STRING_FLAG)) {
                        //目标字段
                        String k = innerKey.substring(0, innerKey.indexOf(":"));
                        realExpr.append(".get(").append(k).append(").getAsString()");
                    } else {
                        realExpr.append(".get(").append(innerKey).append(")");
                    }
                }
                outputParam.setParseExpr(realExpr.toString());
            } else if (outputParam.getParseExpr().contains(EXPR_LIST_FLAG)) {
                outputParam.setOriginParseExpr(outputParam.getParseExpr());

                StringBuilder realExpr = new StringBuilder(DEFAULT_EXPR_JSON_PREX);
                Matcher m = EL_PATTERN.matcher(outputParam.getParseExpr());

                while (m.find()) {
                    String innerKey = m.group(1);
                    if (innerKey.contains(EXPR_LIST_FLAG)) {
                        //目标字段
                        //{data}{goodIds::list[0].string}
                        //turn to params.json().get(data).get(goodIds)|0.string
                        String k = innerKey.substring(0, innerKey.indexOf(":"));
                        String index = innerKey.substring(innerKey.indexOf('[') + 1, innerKey.lastIndexOf(']'));
                        realExpr.append(".get(").append(k).append(")|").append(index);
                        if (innerKey.contains(".")){
                            String valType = innerKey.substring(innerKey.indexOf(".")+1);
                            realExpr.append(".").append(valType);
                        }
                    } else {
                        realExpr.append(".get(").append(innerKey).append(")");
                    }
                }
                outputParam.setParseExpr(realExpr.toString());
            } else if (outputParam.getParseExpr().contains(EXPR_BOOLEAN_FLAG)) {
                //转为布尔值
                outputParam.setOriginParseExpr(outputParam.getParseExpr());

                StringBuilder realExpr = new StringBuilder(DEFAULT_EXPR_JSON_PREX);
                Matcher m = EL_PATTERN.matcher(outputParam.getParseExpr());

                while (m.find()) {
                    String innerKey = m.group(1);
                    if (innerKey.contains(EXPR_BOOLEAN_FLAG)) {
                        //目标字段
                        String k = innerKey.substring(0, innerKey.indexOf(":"));
                        realExpr.append(".get(").append(k).append(").getAsBoolean()");
                    } else {
                        realExpr.append(".get(").append(innerKey).append(")");
                    }
                }
                outputParam.setParseExpr(realExpr.toString());
            } else {
                outputParam.setParseExpr(DEFAULT_EXPR_PREX + outputParam.getParseExpr());
            }
        }
    }
}
