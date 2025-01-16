package run.mone.m78.api.bo.card;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 插槽
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Slot {

    private String type;

    private List<String> children;

    /**
     * 样式
     */
    private Property property;
}
