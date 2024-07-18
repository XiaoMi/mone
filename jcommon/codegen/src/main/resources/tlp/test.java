package run.mone.test.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import run.mone.junit.DoceanConfiguration;
import run.mone.junit.DoceanExtension;
import javax.annotation.Resource;
import run.mone.service.${className}Service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import run.mone.bo.User;
import run.mone.common.Result;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ${author}
 */
@ExtendWith(DoceanExtension.class)
@DoceanConfiguration(basePackage = {"run.mone", "com.xiaomi.youpin.docean.plugin", "com.xiaomi.mone.http.docs.core"})
public class ${className}ServiceTest {


    @Resource
    private ${className}Service ${strutil.toLowerCase(className)}Service;


}