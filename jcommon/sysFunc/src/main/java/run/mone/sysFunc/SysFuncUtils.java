package run.mone.sysFunc;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static run.mone.sysFunc.SysFuncConst.FUNC_NAME_SUBSTRING;
import static run.mone.sysFunc.SysFuncConst.FUNC_NAME_UUID;

public class SysFuncUtils {

    /**
     * eg. ${java.substring("ceshi", 1, 3)}
     *
     * @param funcDesc
     * @return
     */
    public static String gen(String funcDesc) {
        if (StringUtils.isEmpty(funcDesc)
                || !funcDesc.startsWith("${")
                || !funcDesc.endsWith("}")) {
            //不做任何处理，直接返回
            return funcDesc;
        }

        String func = funcDesc.substring(2, funcDesc.length() - 1);
        int index = func.indexOf("(");
        String funcName = func.substring(0, index);
        String funcParams = func.substring(index + 1, func.length() - 1);
        String[] funcParamArr = funcParams.split(",");
        List<String> funcParamList = Arrays.asList(funcParamArr).stream().map(it -> it.trim()).collect(Collectors.toList());

        switch (funcName) {
            case FUNC_NAME_SUBSTRING: {
                return subString(funcParamList, funcParams);
            }
            case FUNC_NAME_UUID: {
                return uuid();
            }
            default:
                return funcDesc;
        }
    }

    private static String subString(List<String> funcParamList, String defaultStr) {

        //substring(int beginIndex)
        if (funcParamList.size() == 2) {
            return funcParamList.get(0).substring(Integer.valueOf(funcParamList.get(1)));
        }

        //substring(int beginIndex, int endIndex)
        if (funcParamList.size() == 3) {
            return funcParamList.get(0).substring(Integer.valueOf(funcParamList.get(1)), Integer.valueOf(funcParamList.get(2)));
        }

        return defaultStr;
    }

    private static String uuid() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }
}
