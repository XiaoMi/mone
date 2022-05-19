package com.xiaomi.mone.file;

import lombok.Data;

import java.io.Serializable;

/**
 * @author goodjava@qq.com
 */
@Data
public class MoneFile implements Serializable {
    

    private boolean isFile;

    private String name;
    
}
