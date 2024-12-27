package run.mone.knowledge.service.common;

/**
 * @author wmin
 * @date 2024/2/6
 */
public class CommonUtils {

    /**
     * 将字符串转为double数组
     * @param str [1.2,3.2]
     * @return
     */
    public static double[] parseDoubleArray(String str) {
        // 移除首尾的方括号并使用逗号分割字符串
        String[] parts = str.substring(1, str.length() - 1).split(",");
        double[] result = new double[parts.length];
        for (int i = 0; i < parts.length; i++) {
            // 将每个字符串转换为 double 类型
            result[i] = Double.parseDouble(parts[i]);
        }
        return result;
    }

}
