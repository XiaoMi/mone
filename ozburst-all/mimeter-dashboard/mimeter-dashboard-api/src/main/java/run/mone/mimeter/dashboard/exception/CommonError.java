package run.mone.mimeter.dashboard.exception;

/**
 * 错误定义
 */
public enum CommonError {

    Success(0, "success"),
    UnknownError(1, "网络错误，请联系管理员"),

    UnknownUser(100000, "用户未登录"),

    // 参数问题
    InvalidParamError(100001, "无效的参数"),
    InvalidIDParamError(100002, "无效的id参数"),
    InvalidPageParamError(100003, "无效的列表请求参数"),
    InvalidUsernameOrPasswdError(100004, "无效的用户名或密码"),
    EmptyFileError(100005, "上传文件为空，请检查"),
    TooLongParamError(100006, "参数过长"),
    WrongFileTypeError(100007, "入参文件仅支持csv格式文件"),
    OverMaxFileSizeError(100008, "上传文件过大"),
    OverMaxFileRowsError(100009, "上传文件行数过大"),
    FileFirstRowEmptyError(100010, "上传文件首行为空"),
    UploadFileError(100011, "上传文件失败"),
    DeleteFileBindingSceneError(100012, "有场景引用该文件，不能删除"),
    MultiDeleteFileBindingSceneError(100013, "有选中文件被场景引用，不能删除"),
    InvalidUserInfoError(100014, "无效的用户信息"),


    SlaRuleAtLeastOneError(1001001, "请至少添加一条SLA规则"),
    DeleteSlaBindingSceneError(1001002, "有场景引用该SLA，不能删除"),

    WrongGatewayEnvError(1002001, "网关环境错误"),


    NewAccountError(200001, "创建账号失败"),
    UnAuthorized(200002, "无权限执行此操作"),
    BeanCopyError(200003, "执行属性拷贝失败"),
    UpdateAccountError(200004, "更新账号失败"),
    DelAccountError(200005, "删除账号失败"),
    AccountNotFoundError(200006, "未找到给定账号信息"),
    AccountExistError(200007, "用户名已经存在"),
    JsonSerializeError(200008, "JSON序列化失败"),
    JsonDeSerializeError(200008, "JSON反序列化失败"),
    APIServerError(200009, "压测引擎任务请求失败"),
    FileSaveError(200010, "文件保存失败"),
    LoadFileError(200011, "读取文件失败"),
    DubboDataTooLong(200012, "数据太大"),
    LoadMiApiDataFail(200013, "加载mi-api接口数据失败"),
    SceneAlreadyExist(200014, "该场景已存在"),

    UnableBenchAgent(200015, "不可用的压测机"),

    UnbindRefDataset(200016, "不允许解绑被引用的数据源"),

    AgentAlreadyApplied(200017, "本组已申请过该发压机"),

    AgentAlreadyGetTenant(200018, "该发压机已有租户"),

    InvalidAgentNumError(200019, "该模式至少绑定2台以上压测机"),

    SystemDeployingError(200020, "Mimeter 系统升级中,请稍后再试~"),
    ;



    public int code;
    public String message;

    public String getMessage() {
        return message;
    }

    CommonError(int code, String message) {
        this.code = code;
        this.message = message;
    }
}
