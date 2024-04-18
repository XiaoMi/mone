package run.mone.local.docean.context;

import lombok.Data;

/**
 * @author goodjava@qq.com
 * @date 2024/2/26 11:55
 */
@Data
public class TianyeContext {

    private Long knowledgeBaseId;

    private String userName;

    private String token;

    private static final class LazyHolder {
        private static final TianyeContext ins = new TianyeContext();
    }


    public static final TianyeContext ins() {
        return LazyHolder.ins;
    }

}
