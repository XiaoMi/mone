package run.mone.ultraman.bo;

import lombok.Data;

/**
 * @author goodjava@qq.com
 * @date 2023/11/5 22:41
 */
@Data
public class AthenaPair<K, V> {

    private K key;

    private V value;


    public static <K, V> AthenaPair<K, V> of(K key, V value) {
        AthenaPair<K, V> a = new AthenaPair<>();
        a.setKey(key);
        a.setValue(value);
        return a;
    }

}
