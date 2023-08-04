package run.mone.match;

import com.gliwka.hyperscan.wrapper.*;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * @author goodjava@qq.com
 * @date 2023/8/3 10:57
 */
public class MatchUtils {


    public static boolean match(String str, String regex) {
        try {
            List<Expression> expressions = new ArrayList<>();
            expressions.add(new Expression(regex, EnumSet.of(ExpressionFlag.SOM_LEFTMOST, ExpressionFlag.CASELESS)));
            Database database = Database.compile(expressions);
            Scanner scanner = new Scanner();
            List<Match> matches = scanner.scan(database, str);
            if (matches.size() > 0) {
                return true;
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return false;
    }


    //ai:输入一个字符串和一个正则表达式,判断是否匹配,返回bool值
    public static boolean isMatch(String str, String regex) {
        return str.matches(regex);
    }


}
