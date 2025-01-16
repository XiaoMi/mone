package run.mone.m78.api.bo.contact;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author dp
 * @date 2024/1/17
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContactParam {

    private String userName;

    private String contactName;

    private String contactEmail;

    private String contactSubject;

    private String contactContent;

}
