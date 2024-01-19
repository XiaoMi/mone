package run.mone.mone.ultraman.grpc;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author goodjava@qq.com
 * @date 2023/8/1 15:10
 */
public class CommonUtils {


    //ai:给你两个List<String>,帮我计算交集
    public List<String> getIntersection(List<String> list1, List<String> list2) {
        return list1.stream().filter(list2::contains).collect(Collectors.toList());
    }


}
