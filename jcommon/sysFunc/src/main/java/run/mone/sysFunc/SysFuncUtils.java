package run.mone.sysFunc;

import org.apache.commons.lang3.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import static run.mone.sysFunc.SysFuncConst.*;
import static run.mone.sysFunc.SysFuncDict.charSeeds;
import static run.mone.sysFunc.SysFuncDict.phoneNumPrefixes;

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
        trimItem(funcParamArr);

        switch (funcName) {
            case FUNC_NAME_SUBSTRING: {
                return subString(funcParamArr, funcParams);
            }
            case FUNC_NAME_UUID: {
                return uuid();
            }
            case FUNC_NAME_RANDOM_NUMBER: {
                return randomNumber(funcParamArr, funcParams);
            }
            case FUNC_NAME_TIME_STAMP: {
                return timeStamp();
            }
            case FUNC_NAME_RANDOM_STRING: {
                return randomString(funcParamArr);
            }
            case FUNC_NAME_PHONE_NUM: {
                return phoneNum();
            }
            case FUNC_NAME_LOWER_CASE: {
                return lowerCase(funcParamArr);
            }
            case FUNC_NAME_UPPER_CASE: {
                return upperCase(funcParamArr);
            }
            case FUNC_NAME_RANDOM_DOUBLE: {
                return randomDouble(funcParamArr);
            }
            case FUNC_NAME_DATE_TO_TIME_STAMP: {
                return dateToTimeStamp(funcParamArr);
            }
            case FUNC_NAME_TIME_STAMP_TO_DATE: {
                return timeStampToDate(funcParamArr);
            }
            case FUNC_NAME_SELECT: {
                return select(funcParamArr);
            }
            default:
                return funcDesc;
        }
    }

    private static void trimItem(String[] items){
        if(items.length==0){
            return;
        }
        for (int i = 0; i < items.length; i++) {
            items[i] = items[i].trim();
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
            IntStream.range(0, batchNumber)
                    .parallel() // 将流转换为并行流
                    .forEach(i -> {
                        // 这里是要并行执行的操作
                        String mockData = supplier.get();
                        res.add(mockData);
                    });
        }

        return res.subList(0, number);
    }

    private static String subString(String[] funcParams, String defaultStr) {

        //substring(int beginIndex)
        if (funcParams.length == 2) {
            return funcParams[0].substring(Integer.parseInt(funcParams[1]));
        }

        //substring(int beginIndex, int endIndex)
        if (funcParams.length == 3) {
            return funcParams[0].substring(Integer.parseInt(funcParams[1]), Integer.parseInt(funcParams[2]));
        }
        return defaultStr;
    }

    private static String uuid() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    private static String randomNumber(String[] funcParams, String defaultStr) {
        if (funcParams.length == 2) {
            return String.valueOf(ThreadLocalRandom.current().nextLong(Long.parseLong(funcParams[0]), Long.parseLong(funcParams[1])));
        }

        if (funcParams.length == 1) {
            return String.valueOf(ThreadLocalRandom.current().nextLong(Long.parseLong(funcParams[0])));
        }

        return defaultStr;
    }

    private static String timeStamp(){
        return String.valueOf(System.currentTimeMillis());
    }

    private static String randomString(String[] funcParams){
        if (funcParams.length == 0) {
            return "";
        }
        int range = Integer.parseInt(funcParams[0]);
        if (range <= 0) {
            return "";
        }
        ThreadLocalRandom localRandom = ThreadLocalRandom.current();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < range; i++) {
            builder.append(charSeeds[localRandom.nextInt(charSeeds.length)]);
        }
        return builder.toString();
    }

    private static String phoneNum(){
        ThreadLocalRandom localRandom = ThreadLocalRandom.current();
        StringBuilder builder = new StringBuilder(phoneNumPrefixes[ThreadLocalRandom.current().nextInt(phoneNumPrefixes.length)]);
        for (int i = 0; i < 8; i++) {
            builder.append(localRandom.nextInt(10));
        }
        return builder.toString();
    }

    private static String upperCase(String[] funcParams){
        if(funcParams == null || funcParams.length == 0){
            return "";
        }
        return funcParams[0].toUpperCase();
    }

    private static String lowerCase(String[] funcParams){
        if(funcParams == null || funcParams.length == 0){
            return "";
        }
        return funcParams[0].toLowerCase();
    }

    private static String select(String[] funcParams){
        if(funcParams.length==0){
            return "";
        }
        if(funcParams.length==1){
            return funcParams[0];
        }
        return funcParams[ThreadLocalRandom.current().nextInt(funcParams.length)];
    }

    private static String randomDouble(String[] funcParams){
        if( funcParams.length != 3){
            return "0.0";
        }
        int decimalPlaces = Integer.parseInt(funcParams[2]);
        double v = ThreadLocalRandom.current().nextDouble(Double.parseDouble(funcParams[0]), Double.parseDouble(funcParams[1]));
        return String.format("%." + decimalPlaces + "f", v);
    }

    private static String timeStampToDate(String[] funcParams){
        if(funcParams == null || funcParams.length < 2){
            return null;
        }
        String data = funcParams[0];
        String format = funcParams[1];
        return new SimpleDateFormat(format).format(new Date(Long.parseLong(data)));
    }

    private static String dateToTimeStamp(String[] funcParams){
        if(funcParams == null || funcParams.length < 2){
            return null;
        }
        String data = funcParams[0];
        String format = funcParams[1];
        try {
            return String.valueOf(new SimpleDateFormat(format).parse(data).getTime());
        } catch (ParseException e) {
            System.out.println("dateToTimeStamp fail ,error ="+ e);
        }
        return null;
    }
}
