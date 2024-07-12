package run.mone.service;

import com.xiaomi.youpin.docean.anno.Service;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import run.mone.model.po.${className};


/**
 * @author ${author}
 */
@Data
@Service
@Slf4j
public class ${className}Service extends MongoService<${className}> {

    public ${className}Service() {
        super(${className}.class);
    }
}