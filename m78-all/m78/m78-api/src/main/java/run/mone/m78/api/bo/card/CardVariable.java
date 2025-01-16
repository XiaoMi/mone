package run.mone.m78.api.bo.card;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 变量
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class CardVariable {

    private Long id;

    private Long cardId;

    private String name;

    private String classType;

    private String defaultValue;

    private String creator;

    private String updater;

    private Long ctime;

    private Long utime;
}
