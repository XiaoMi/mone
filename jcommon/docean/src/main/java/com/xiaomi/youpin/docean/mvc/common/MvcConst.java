package com.xiaomi.youpin.docean.mvc.common;

import com.xiaomi.youpin.docean.mvc.MvcContext;

/**
 * @author goodjava@qq.com
 * @date 2022/12/22 12:09
 */
public abstract class MvcConst {

    public static final String CGLIB = "$cglib";

    public static final String STATIC_FILE_PATH = "$staticFilePath";

    public static final String OPEN_STATIC_FILE = "$openStaticFile";

    public static final String TRUE = "true";

    public static final String EMPTY = "";

    public static final String FALSE = "false";

    public static final String ALLOW_CROSS_DOMAIN = "$allow-cross-domain";

    public static final String RESPONSE_ORIGINAL_VALUE = "$response-original-value";

    public static final String MVC_DOWNLOAD = "$mvc-download";

    public static final String MVC_POOL_SIZE = "$mvc-pool-size";

    public static final String VIRTUAL_THREAD = "$virtual-threaad";

    public static final int DEFAULT_MVC_POOL_SIZE = 200;

    public static ScopedValue<MvcContext> MVC_CONTEXT = ScopedValue.newInstance();

}
