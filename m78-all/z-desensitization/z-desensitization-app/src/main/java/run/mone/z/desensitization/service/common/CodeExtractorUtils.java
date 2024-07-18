package run.mone.z.desensitization.service.common;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Pair;

import java.util.Stack;

/**
 * @author wmin
 * @date 2023/6/7
 */
@Slf4j
public class CodeExtractorUtils {

    /**
     * 只能处理单段代码
     * 代码提取，返回代码片段开始和结束的index
     */
    public static Pair<Boolean, String> codeExtractor(String str) {
        Stack<Character> stack = new Stack<>();
        char[] arr = str.toCharArray();
        int firstBra = -1;
        int lastBra = -1;
        for (int i=0;i<arr.length;i++){
            char c = arr[i];
            if (firstBra == -1 && c == '{'){
                firstBra = i;
            }
            if (c == '}'){
                lastBra = i;
            }
            if (c == '(' || c == '{') {
                stack.push(c);
            } else if (c == ')' || c == '}') {
                if (stack.isEmpty()) {
                    return Pair.of(false,"");
                }
                char top = stack.pop();
                if ((c == ')' && top != '(') || (c == '}' && top != '{')) {
                    return Pair.of(false,"");
                }
            }
        }
        //往前找到第一个public/private/protected
        int publicIndex = str.lastIndexOf("public", firstBra);
        int privateIndex = str.lastIndexOf("private", firstBra);
        int protectedIndex = str.lastIndexOf("protected", firstBra);
        int codeStartIndex = Math.max(publicIndex, Math.max(privateIndex, protectedIndex));
        if (codeStartIndex == -1) {
            return Pair.of(false,"");
        }
        if (stack.isEmpty()){
            return Pair.of(true, codeStartIndex+"-"+lastBra);
        }
        return Pair.of(false,"");
    }

    /**
     * 代码提取，返回代码片段开始和结束的index
     */
    public static Pair<Boolean, String> codeExtractorWithLabel(String str) {
        if (str.contains(Consts.codeExtractorStartLabel) && str.contains(Consts.codeExtractorEndLabel)){
            int startIndex = str.indexOf(Consts.codeExtractorStartLabel);
            int endIndex = str.indexOf(Consts.codeExtractorEndLabel, startIndex + Consts.codeExtractorStartLabel.length());
            return Pair.of(true, startIndex + Consts.codeExtractorStartLabel.length()+"-"+endIndex);
        }
        return Pair.of(false,"not supported");
    }
}
