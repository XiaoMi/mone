package run.mone.ultraman.bo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author goodjava@qq.com
 * @date 2023/6/24 21:33
 */
@Data
public class PackageInfo implements Serializable,NameInfo {

    private String name;

}
