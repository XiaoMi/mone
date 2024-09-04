package ${testPackageName};

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import javax.annotation.Resource;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;

/**
 * @author ${testAuthor}
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ${mainClass}.class)
public class ${testName} {


    @Resource
    private ${serviceName} ${className}Service;


}