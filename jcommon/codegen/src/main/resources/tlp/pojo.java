package run.mone.model.po;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import run.mone.bo.MongoBo;

/**
 * @author ${author}
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ${className} implements MongoBo {

    @Id
    private String id;

    private long ctime;

    private long utime;

    private int state;

    private int version;

}