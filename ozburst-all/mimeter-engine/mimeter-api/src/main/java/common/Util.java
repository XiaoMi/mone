package common;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.bind.ObjectTypeAdapter;
import com.xiaomi.youpin.docean.common.Pair;

import java.lang.reflect.Field;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util {

    private static final Pattern EL_PATTERN = Pattern.compile("\\$\\{(.*)\\}");

    public static Gson getGson() {
        Gson gson = new GsonBuilder().serializeNulls().enableComplexMapKeySerialization().disableHtmlEscaping().create();
        try {
            Field factories = Gson.class.getDeclaredField("factories");
            factories.setAccessible(true);
            Object o = factories.get(gson);
            Class<?>[] declaredClasses = Collections.class.getDeclaredClasses();
            for (Class c : declaredClasses) {
                if ("java.util.Collections$UnmodifiableList".equals(c.getName())) {
                    Field listField = c.getDeclaredField("list");
                    listField.setAccessible(true);
                    List<TypeAdapterFactory> list = (List<TypeAdapterFactory>) listField.get(o);
                    int i = list.indexOf(ObjectTypeAdapter.FACTORY);
                    list.set(i, MapTypeAdapter.FACTORY);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return gson;
    }

    public static class Parser {

        /**
         * 将字符串text中由openToken和closeToken组成的占位符依次替换为args数组中的值
         */
        public static String parse(String openToken, String closeToken, String text, Object... args) {
            if (args == null || args.length <= 0) {
                return text;
            }
            int argsIndex = 0;

            if (text == null || text.isEmpty()) {
                return "";
            }
            char[] src = text.toCharArray();
            int offset = 0;
            // search open token
            int start = text.indexOf(openToken, offset);
            if (start == -1) {
                return text;
            }
            final StringBuilder builder = new StringBuilder();
            StringBuilder expression = null;
            int replaceCount = 0;
            while (start > -1) {
                if (start > 0 && src[start - 1] == '\\') {
                    // this open token is escaped. remove the backslash and continue.
                    builder.append(src, offset, start - offset - 1).append(openToken);
                    offset = start + openToken.length();
                } else {
                    // found open token. let's search close token.
                    if (expression == null) {
                        expression = new StringBuilder();
                    } else {
                        expression.setLength(0);
                    }
                    builder.append(src, offset, start - offset);
                    offset = start + openToken.length();
                    int end = text.indexOf(closeToken, offset);
                    while (end > -1) {
                        if (end > offset && src[end - 1] == '\\') {
                            // this close token is escaped. remove the backslash and continue.
                            expression.append(src, offset, end - offset - 1).append(closeToken);
                            offset = end + closeToken.length();
                            end = text.indexOf(closeToken, offset);
                        } else {
                            expression.append(src, offset, end - offset);
                            break;
                        }
                    }
                    if (end == -1) {
                        // close token was not found.
                        builder.append(src, start, src.length - start);
                        offset = src.length;
                    } else {
                        String value = (argsIndex <= args.length - 1) ?
                                (args[argsIndex] == null ? "" : args[argsIndex].toString()) : expression.toString();
                        //数组数据源标识 ["${list}"]  "LF1,2,3"
                        boolean listNum = false;
                        if (value.startsWith("LF")) {
                            if (value.charAt(2) != '"') {
                                //数组 纯数字
                                value = value.substring(2);
                                listNum = true;
                            } else {
                                //数组 字符串
                                value = value.substring(3, value.length() - 1);
                            }
                        }
                        if (value.startsWith("s^")) {
                            value = value.substring(2);
                        }
                        assert args[0] != null;
                        if (args[0].getClass().getName().startsWith("java.lang.Long") || args[0].getClass().getName().startsWith("java.lang.Double") || listNum) {
                            builder.deleteCharAt(builder.length() - 1);
                            offset = end + closeToken.length() + 1;
                        } else {
                            offset = end + closeToken.length();
                        }
                        builder.append(value);
                        argsIndex++;
                    }
                }
                start = text.indexOf(openToken, offset);
                replaceCount++;
                if (replaceCount >= args.length) {
                    break;
                }
            }
            if (offset < src.length) {
                builder.append(src, offset, src.length - offset);
            }
            return builder.toString();
        }

        public static String parse$(String key, String text, Object... args) {
            return Parser.parse("${" + key, "}", text, args);
        }


        public static String parse1(String text, Object... args) {
            return Parser.parse("{", "}", text, args);
        }
    }


    public static Pair<String, String> getElKey(String key) {
        String k = null;
        String v = null;
        if (null != key && key.length() > 0) {
            Matcher m = EL_PATTERN.matcher(key);

            if (m.find()) {
                k = m.group(1);
                v = "";
            }
        }
        return Pair.of(k, v);
    }

    /**
     * 按比率判断
     *
     * @param rate 0~100
     * @return
     */
    public static boolean judgeByRate(int rate) {
        Random random = new Random();
        return random.nextInt(100) <= rate;
    }

    /**
     * 按比率判断
     *
     * @param rate 0~1000
     * @return
     */
    public static boolean judgeByRateLog(int rate) {
        Random random = new Random();
        return random.nextInt(1000) <= rate;
    }

    public static int sampleRateToCnt(int n, int rate) {
        return (int) Math.ceil(n * rate / 1000.0d);
    }

    // fix inaccurate log rate due to low qps, so use (qps * Math.min(10, duration)) as base
    public static List<Integer> sampleByTimeAndQps(int duration, int qps, int rate) {
        int base = Math.min(10, duration) * qps;
        return sampleIndices(base, sampleRateToCnt(base, rate));
    }

    /**
     * 从[0, n)随机抽取出k个数, k <= n
     */
    public static List<Integer> sampleIndices(int n, int k) {
        List<Integer> output = new ArrayList<>();

        // will use judgeByRate() as backup
        if (k <= 0 || n < k) {
            return output;
        }
        int i, j;

        for (i = 0; i < k; i++) {
            output.add(i);
        }
        Random rand = new Random();

        for (j = i; j < n; j++) {
            int index = rand.nextInt(j + 1);

            if (index < k) {
                output.set(index, j);
            }
        }
        return output;
    }

    public static Object getListValByType(JsonArray valList, int index, String type) {
        Object value;
        switch (type) {
            case "string" -> value = valList.get(index).getAsString();
            case "int" -> value = valList.get(index).getAsInt();
            case "double" -> value = valList.get(index).getAsDouble();
            default -> value = valList.get(index);
        }
        return value;
    }

    public static int calculateAgentRps(int totalRps, int agentNum) {
        int agentQps = totalRps / agentNum;
        if (agentQps < 1) {
            agentQps = 1;
        } else if (agentQps > Const.AGENT_MAX_RPS) {
            //单台最多给15000 rps
            agentQps = Const.AGENT_MAX_RPS;
        }
        return agentQps;
    }
}
