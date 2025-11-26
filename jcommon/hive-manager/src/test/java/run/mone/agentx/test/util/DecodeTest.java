package run.mone.agentx.test.util;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class DecodeTest {

    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Test
    public void testDecode() {
        String encoded = passwordEncoder.encode("T7m$pL9@qR2v");
        System.out.println(encoded);
    }
}
