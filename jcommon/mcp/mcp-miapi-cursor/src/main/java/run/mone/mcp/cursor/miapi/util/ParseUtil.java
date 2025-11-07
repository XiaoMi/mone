package run.mone.mcp.cursor.miapi.util;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.expr.SingleMemberAnnotationExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import run.mone.mcp.cursor.miapi.model.ApiInfo;
import run.mone.mcp.cursor.miapi.model.ParameterInfo;
import run.mone.mcp.cursor.miapi.model.ParserResult;
import run.mone.mcp.cursor.miapi.parser.ClassFieldExtractor;

import java.util.ArrayList;
import java.util.List;

public class ParseUtil {
    private static final Logger logger = LoggerFactory.getLogger(ParseUtil.class);

    /**
     * 解析编译单元
     */
    public static void parseCompilationUnit(CompilationUnit cu, ParserResult result, String codeRoot) {
        cu.accept(new VoidVisitorAdapter<CompilationUnit>() {
            @Override
            public void visit(ClassOrInterfaceDeclaration n, CompilationUnit arg) {
                if (isControllerClass(n)) {
                    String className = n.getNameAsString();
                    String qualifiedName = n.getFullyQualifiedName().orElse("");

                    // 解析Controller级别的路径
                    String controllerPath = getControllerPath(n);

                    // 解析方法
                    for (MethodDeclaration method : n.getMethods()) {
                        if (isApiMethod(method)) {
                            ApiInfo apiInfo = parseApiMethod(method, className, qualifiedName, controllerPath, codeRoot);
                            if (apiInfo != null) {
//                                result.addApi(apiInfo);
                                result.addApiToGroup(qualifiedName, apiInfo);
//                                result.addApiToPath(apiInfo.getPath(), apiInfo);
                                result.setParsedApiCount(result.getParsedApiCount() + 1);
                            }
                        }
                    }

                    result.setParsedFileCount(result.getParsedFileCount() + 1);
                    super.visit(n, arg);
                }
            }

        }, null);
    }

    /**
     * 检查是否是Controller类
     */
    public static boolean isControllerClass(ClassOrInterfaceDeclaration n) {
        return n.getAnnotationByName("Controller").isPresent() ||
                n.getAnnotationByName("RestController").isPresent() ||
                n.getAnnotationByName("RequestMapping").isPresent();
    }

    /**
     * 获取Controller级别的路径
     */
    public static String getControllerPath(ClassOrInterfaceDeclaration n) {
        return n.getAnnotationByName("RequestMapping")
                .map(annotation -> getAnnotationValue(annotation, "value"))
                .orElse("");
    }

    /**
     * 检查是否是API方法
     */
    public static boolean isApiMethod(MethodDeclaration method) {
        return method.getAnnotationByName("RequestMapping").isPresent() ||
                method.getAnnotationByName("GetMapping").isPresent() ||
                method.getAnnotationByName("PostMapping").isPresent() ||
                method.getAnnotationByName("PutMapping").isPresent() ||
                method.getAnnotationByName("DeleteMapping").isPresent() ||
                method.getAnnotationByName("PatchMapping").isPresent();
    }

    /**
     * 解析API方法
     */
    public static ApiInfo parseApiMethod(MethodDeclaration method, String className, String qualifiedName, String controllerPath, String codeRoot) {
        ApiInfo apiInfo = new ApiInfo();

        // 设置基本信息
        apiInfo.setName(method.getNameAsString());
        apiInfo.setControllerClass(className);
        apiInfo.setControllerQualifiedName(qualifiedName);
        apiInfo.setMethodSignature(method.getSignature().asString());
        apiInfo.setDeprecated(method.getAnnotationByName("Deprecated").isPresent());

        // 解析请求映射
        parseRequestMapping(method, apiInfo, controllerPath);

        // 解析参数
        parseParameters(method, apiInfo, codeRoot);

        // 解析返回值
        parseReturnType(method, apiInfo, codeRoot);

        return apiInfo;
    }

    /**
     * 解析请求映射
     */
    public static void parseRequestMapping(MethodDeclaration method, ApiInfo apiInfo, String controllerPath) {
        String methodType = "GET";
        String path = "";

        if (method.getAnnotationByName("GetMapping").isPresent()) {
            methodType = "GET";
            path = getAnnotationValue(method.getAnnotationByName("GetMapping").get(), "value");
        } else if (method.getAnnotationByName("PostMapping").isPresent()) {
            methodType = "POST";
            path = getAnnotationValue(method.getAnnotationByName("PostMapping").get(), "value");
        } else if (method.getAnnotationByName("PutMapping").isPresent()) {
            methodType = "PUT";
            path = getAnnotationValue(method.getAnnotationByName("PutMapping").get(), "value");
        } else if (method.getAnnotationByName("DeleteMapping").isPresent()) {
            methodType = "DELETE";
            path = getAnnotationValue(method.getAnnotationByName("DeleteMapping").get(), "value");
        } else if (method.getAnnotationByName("PatchMapping").isPresent()) {
            methodType = "PATCH";
            path = getAnnotationValue(method.getAnnotationByName("PatchMapping").get(), "value");
        } else if (method.getAnnotationByName("RequestMapping").isPresent()) {
            AnnotationExpr annotation = method.getAnnotationByName("RequestMapping").get();
            path = getAnnotationValue(annotation, "value");
            if (path.isEmpty()) {
                path = getAnnotationValue(annotation, "path");
            }
            String methodValue = getAnnotationValue(annotation, "method");
            if (!methodValue.isEmpty()) {
                methodType = methodValue;
            }
        }

        // 组合完整路径
        String fullPath = controllerPath + path;
        if (!fullPath.startsWith("/")) {
            fullPath = "/" + fullPath;
        }

        apiInfo.setMethod(methodType);
        apiInfo.setPath(fullPath);
    }

    public static void deepField (Type type, ParameterInfo parameterInfo, String codeRoot) {
        List<ParameterInfo> children = new ArrayList<>();
        parameterInfo.setChildList(children);
        try {
            if (!TypeExtractorUtil.isInternalType(type)) {
                // 如果是com.xx.xx.Result<String>会有问题,childNodes中为com.xx.xx,Result,String
                List<Node> childNodes = ((Node) type).getChildNodes();
                List<Node> nodes = new ArrayList<>();
                if (childNodes != null && childNodes.size()>0) {
                    nodes = childNodes.subList(1, childNodes.size());
                }
                String className = ((ClassOrInterfaceType)type).getName().asString();
//                List<String> list = TypeExtractorUtil.extractTypesByLevel(type.asString());
                if (className != null) {
                    List<ClassFieldExtractor.FieldInfo> fields = ClassFieldExtractor.findClassAndExtractFields(className, codeRoot, nodes);
                    if (fields.size() > 0) {
                        List<ParameterInfo> parameterInfoList = new ArrayList<>();
                        for (ClassFieldExtractor.FieldInfo field : fields) {
                            ParameterInfo info = new ParameterInfo();
                            info.setName(field.getFieldName());
                            info.setDescription(field.getComment());
                            info.setGenericType(String.join(",",field.getGenericTypes()));
                            info.setType(TypeExtractorUtil.typeStr2TypeNo(field.getFieldType()));
                            deepField(field.getClassType(), info, codeRoot);
                            parameterInfoList.add(info);
                        }
                        parameterInfo.setChildList(parameterInfoList);
                    } else {
                        parameterInfo.setType(TypeExtractorUtil.typeStr2TypeNo(TypeExtractorUtil.getPrimitiveSimpleName(type)));
                    }
                }
            } else {
                parameterInfo.setType(TypeExtractorUtil.typeStr2TypeNo(TypeExtractorUtil.getPrimitiveSimpleName(type)));
            }
        }catch (Exception e) {
            logger.error("deepField error: ", e);
        }
    }

    /**
     * 解析参数
     */
    public static void parseParameters(MethodDeclaration method, ApiInfo apiInfo, String codeRoot) {
        List<ParameterInfo> parameters = new ArrayList<>();

        method.getParameters().forEach(param -> {
            ParameterInfo paramInfo = new ParameterInfo();
            paramInfo.setName(param.getNameAsString());
            paramInfo.setClassType(param.getType());
            paramInfo.setPosition("body"); // 默认位置
            deepField(param.getType(), paramInfo, codeRoot);
            // 解析参数注解
            if (param.getAnnotationByName("RequestParam").isPresent()) {
                paramInfo.setPosition("query");
                String value = getAnnotationValue(param.getAnnotationByName("RequestParam").get(), "value");
                if (!value.isEmpty()) {
                    paramInfo.setName(value);
                }
                paramInfo.setRequired(getAnnotationBooleanValue(param.getAnnotationByName("RequestParam").get(), "required", true));
            } else if (param.getAnnotationByName("PathVariable").isPresent()) {
                paramInfo.setPosition("path");
                String value = getAnnotationValue(param.getAnnotationByName("PathVariable").get(), "value");
                if (!value.isEmpty()) {
                    paramInfo.setName(value);
                }
                paramInfo.setRequired(true);
            } else if (param.getAnnotationByName("RequestBody").isPresent()) {
                paramInfo.setPosition("body");
                paramInfo.setRequired(true);
            } else if (param.getAnnotationByName("RequestHeader").isPresent()) {
                paramInfo.setPosition("header");
                String value = getAnnotationValue(param.getAnnotationByName("RequestHeader").get(), "value");
                if (!value.isEmpty()) {
                    paramInfo.setName(value);
                }
                paramInfo.setRequired(getAnnotationBooleanValue(param.getAnnotationByName("RequestHeader").get(), "required", true));
            }

            parameters.add(paramInfo);
        });

        apiInfo.setInputParameters(parameters);
    }

    /**
     * 解析返回值类型
     */
    public static void parseReturnType(MethodDeclaration method, ApiInfo apiInfo, String codeRoot) {
        String returnType = method.getType().asString();
        apiInfo.setReturnType(returnType);
        apiInfo.setReturnDescription("返回值类型: " + returnType);
        ParameterInfo paramInfo = new ParameterInfo();
        paramInfo.setName("root");
        paramInfo.setClassType(method.getType());
        deepField(method.getType(), paramInfo, codeRoot);
        apiInfo.setReturnFields(List.of(paramInfo));
    }
    /**
     * 获取注解值
     */
    public static String getAnnotationValue(AnnotationExpr annotation, String key) {
        if (annotation instanceof NormalAnnotationExpr) {
            NormalAnnotationExpr normalAnnotation = (NormalAnnotationExpr) annotation;
            return normalAnnotation.getPairs().stream()
                    .filter(pair -> pair.getNameAsString().equals(key))
                    .findFirst()
                    .map(pair -> {
                        if (pair.getValue() instanceof StringLiteralExpr) {
                            return ((StringLiteralExpr) pair.getValue()).getValue();
                        }
                        return pair.getValue().toString().replaceAll("\"", "");
                    })
                    .orElse("");
        } else if (annotation instanceof SingleMemberAnnotationExpr) {
            SingleMemberAnnotationExpr singleAnnotation = (SingleMemberAnnotationExpr) annotation;
            if (singleAnnotation.getMemberValue() instanceof StringLiteralExpr) {
                return ((StringLiteralExpr) singleAnnotation.getMemberValue()).getValue();
            }
            return singleAnnotation.getMemberValue().toString().replaceAll("\"", "");
        }
        return "";
    }

    /**
     * 获取注解布尔值
     */
    public static boolean getAnnotationBooleanValue(AnnotationExpr annotation, String key, boolean defaultValue) {
        if (annotation instanceof NormalAnnotationExpr) {
            NormalAnnotationExpr normalAnnotation = (NormalAnnotationExpr) annotation;
            return normalAnnotation.getPairs().stream()
                    .filter(pair -> pair.getNameAsString().equals(key))
                    .findFirst()
                    .map(pair -> Boolean.parseBoolean(pair.getValue().toString()))
                    .orElse(defaultValue);
        }
        return defaultValue;
    }

    /**
     * 合并解析结果
     */
    public static void mergeResults(ParserResult target, ParserResult source) {
        // 合并按Controller分组的结果
        for (String key : source.getApiGroups().keySet()) {
            target.getApiGroups().computeIfAbsent(key, k -> new ArrayList<>())
                    .addAll(source.getApiGroups().get(key));
        }

        // 更新统计信息
        target.setParsedFileCount(target.getParsedFileCount() + source.getParsedFileCount());
        target.setParsedApiCount(target.getParsedApiCount() + source.getParsedApiCount());
    }

    /**
     * 处理相同地址的区分
     */
    public static void processDuplicatePaths(ParserResult result) {

    }
}
