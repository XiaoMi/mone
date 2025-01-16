package run.mone.m78.api.bo.im;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigInteger;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class IMRecordDTO implements Serializable {

    private BigInteger id;

    private BigInteger botId;

    private String chatId;

    private Integer imTypeId;

    private String userName;

    private Integer status;

}
