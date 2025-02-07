package run.mone.m78.api.constant;

/**
 * @author HawickMason@xiaomi.com
 * @date 2/6/24 11:14
 */
public class FeatureRouterConstant {

    public static final String DATASOURCE_ID = "datasourceId";

    public static final String ROUTER_META_SQL = "$rM_sql$";

    public static final String ROUTER_META_TYPE_MARK = "@type";

    public static final String ROUTER_META_STORE = "$rM_store$";

    public static final String ROUTER_META_TYPE = "$rM_storeType$";

    public static final String ROUTER_TYPE_C = "C";

    public static final String ROUTER_TYPE_R = "R";

    public static final String ROUTER_TYPE_U = "U";

    public static final String ROUTER_TYPE_D = "D";
    public static final String CURL_PREFIX = "curl -H 'content-type: application/json' ";

    public static final String CURL_SUFFIX = "/open-apis/ai-plugin-new/feature/router/query";

    public static final String CURL_SUFFIX_BOT = "/open-apis/ai-plugin-new/feature/router/probot/query";

    public static final String ROUTER_NAME_PREFIX_FOR_C = "新增记录_";

    public static final String ROUTER_NAME_PREFIX_FOR_U = "更新记录_";

    public static final String ROUTER_NAME_PREFIX_FOR_D = "删除记录_";

}
