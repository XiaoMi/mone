package run.mone.mfa.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * MFA状态请求
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MfaStatusRequest {
    private boolean enabled;
    private String code;
} 
