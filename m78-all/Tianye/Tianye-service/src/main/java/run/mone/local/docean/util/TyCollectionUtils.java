package run.mone.local.docean.util;

import java.util.List;
import java.util.Objects;

/**
 * @author HawickMason@xiaomi.com
 * @date 9/6/24 14:04
 */
public class TyCollectionUtils {

    // 判断一个元素是否在提供的list中，如果存在返回下表，否则返回-1
    public static <T> int indexOf(List<T> list, T element) {
        if (list == null || list.isEmpty()) {
            return -1;
        }

        for (int i = 0; i < list.size(); i++) {
            if (Objects.equals(element, list.get(i))) {
                return i;
            }
        }

        return -1;
    }
}
