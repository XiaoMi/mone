package run.mone.m78.api.bo.datasource;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @author goodjava@qq.com
 * @date 2024/2/6 16:23
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class SqlQueryRes implements Serializable {

    private int total;

    private List<Map<String, Object>> data;

}
