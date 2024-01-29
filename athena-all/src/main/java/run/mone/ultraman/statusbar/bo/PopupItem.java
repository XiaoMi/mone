package run.mone.ultraman.statusbar.bo;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author goodjava@qq.com
 * @date 2023/7/19 10:54
 */
@Data
@Builder
public class PopupItem implements Serializable {

    private String name;

    private String desc;

    @Override
    public String toString() {
        return this.desc;
    }
}
