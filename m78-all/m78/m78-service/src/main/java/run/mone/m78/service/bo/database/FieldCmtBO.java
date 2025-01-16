package run.mone.m78.service.bo.database;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author HawickMason@xiaomi.com
 * @date 1/16/24 09:59
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class FieldCmtBO {

    private String fieldName;

    private String comment;
}
