package run.mone.sysFunc;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static run.mone.sysFunc.SysFuncConst.*;

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
            case FUNC_NAME_RANDOM_NUMBER: {
                return randomNumber(funcParamList, funcParams);
            }
            default:
                return funcDesc;
        }
    }

    public static List<String> batchGen(String funcDesc, int number) {
        return batchOperation(() -> gen(funcDesc), number);
    }

    public static List<String> batchOperation(Supplier<String> supplier, int number) {
        List<String> res = new CopyOnWriteArrayList<>();

        int batchNumber = 1000;
        int n = (number / batchNumber) + 1;

        for (int j = 0; j <= n; j++) {
            int defaultNumber = batchNumber;
            IntStream.range(0, defaultNumber)
                    .parallel() // 将流转换为并行流
                    .forEach(i -> {
                        // 这里是要并行执行的操作
                        String mockData = supplier.get();
                        res.add(mockData);
                    });
        }

        return res.subList(0, number);
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

    private static String randomNumber(List<String> funcParamList, String defaultStr) {
        if (funcParamList.size() == 2) {
            int randomNumberInRange = ThreadLocalRandom.current().nextInt(Integer.valueOf(funcParamList.get(0)), Integer.valueOf(funcParamList.get(1)));
            return String.valueOf(randomNumberInRange);
        }

        if (funcParamList.size() == 1) {
            int randomNumberInRange = ThreadLocalRandom.current().nextInt(Integer.valueOf(funcParamList.get(0)));
            return String.valueOf(randomNumberInRange);
        }

        return defaultStr;
    }
}
