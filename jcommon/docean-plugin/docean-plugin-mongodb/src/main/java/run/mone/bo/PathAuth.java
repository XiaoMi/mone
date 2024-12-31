package run.mone.bo;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author goodjava@qq.com
 * @date 2024/4/22 21:35
 */
@Entity("pathAuth")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PathAuth {

    @Id
    private String id;

    private String path;

    private String role;

}
