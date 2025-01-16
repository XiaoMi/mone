package run.mone.m78.api.bo.card;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 基础
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CardElement {

    private Long id;

    private Long workspaceId;

    private Long cardId;

    private String uniqueKey;

    private String type;

    private Property property;

    private List<String> children;
}
