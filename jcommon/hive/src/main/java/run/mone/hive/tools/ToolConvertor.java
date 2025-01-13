package run.mone.hive.tools;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import lombok.extern.slf4j.Slf4j;
import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.javadoc.Javadoc;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

@Slf4j
public class ToolConvertor {
    
    public static Map<String, Object> convertCodeToToolSchema(Object obj, List<String> include) {
        if (obj == null) {
            return new HashMap<>();
        }

        Class<?> clazz = obj instanceof Class ? (Class<?>) obj : obj.getClass();
        String docString = getClassJavadoc(clazz);

        Map<String, Object> schema = new HashMap<>();
        
        if (obj instanceof Class) {
            schema.put("type", "class");
            schema.put("description", cleanDocString(docString));
            
            Map<String, Object> methods = new HashMap<>();
            for (Method method : clazz.getDeclaredMethods()) {
                if (shouldSkipMethod(method, include)) {
                    continue;
                }
                String methodDoc = getMethodJavadoc(method);
                if (methodDoc != null) {
                    methods.put(method.getName(), functionDocstringToSchema(method, methodDoc));
                }
            }
            schema.put("methods", methods);
        } else {
            schema = functionDocstringToSchema(obj.getClass().getDeclaredMethods()[0], docString);
        }

        return schema;
    }

    public static Map<String, Map<String, Object>> convertCodeToToolSchemaAst(String code) {
        Map<String, Map<String, Object>> schemas = new HashMap<>();
        
        try {
            JavaParser parser = new JavaParser();
            CompilationUnit cu = parser.parse(code).getResult().orElse(null);
            if (cu == null) {
                return schemas;
            }

            CodeVisitor visitor = new CodeVisitor(code);
            visitor.visit(cu, null);
            schemas = visitor.getToolSchemas();
            
        } catch (Exception e) {
            log.error("Failed to parse code: {}", e.getMessage());
        }
        
        return schemas;
    }

    private static Map<String, Object> functionDocstringToSchema(Method method, String docString) {
        Map<String, Object> schema = new HashMap<>();
        
        schema.put("type", method.isSynthetic() ? "async_function" : "function");
        
        Javadoc javadoc = StaticJavaParser.parseJavadoc(docString);
        
        schema.put("description", javadoc.getDescription().toText().trim());
        schema.put("signature", getFunctionSignature(method));
        
        Map<String, String> params = new HashMap<>();
        javadoc.getBlockTags().forEach(tag -> {
            if (tag.getTagName().equals("param")) {
                String[] parts = tag.getContent().toText().split("\\s+", 2);
                if (parts.length == 2) {
                    params.put(parts[0], parts[1].trim());
                }
            }
        });
        schema.put("parameters", params);
        
        return schema;
    }

    private static String getFunctionSignature(Method method) {
        StringBuilder signature = new StringBuilder();
        signature.append("(");
        
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            Parameter param = parameters[i];
            signature.append(param.getType().getSimpleName())
                    .append(" ")
                    .append(param.getName());
            if (i < parameters.length - 1) {
                signature.append(", ");
            }
        }
        
        signature.append(") -> ")
                .append(method.getReturnType().getSimpleName());
        
        return signature.toString();
    }

    private static boolean shouldSkipMethod(Method method, List<String> include) {
        String name = method.getName();
        return (name.startsWith("_") && !name.equals("__init__")) ||
                (include != null && !include.contains(name));
    }

    private static String getClassJavadoc(Class<?> clazz) {
        try {
            return clazz.getAnnotation(Documentation.class).value();
        } catch (Exception e) {
            return "";
        }
    }

    private static String getMethodJavadoc(Method method) {
        try {
            return method.getAnnotation(Documentation.class).value();
        } catch (Exception e) {
            return null;
        }
    }

    private static String cleanDocString(String docString) {
        return docString.replaceAll("\\s+", " ").trim();
    }
}

@Slf4j
class CodeVisitor {
    private final String sourceCode;
    private final Map<String, Map<String, Object>> toolSchemas = new HashMap<>();

    public CodeVisitor(String sourceCode) {
        this.sourceCode = sourceCode;
    }

    public void visit(CompilationUnit cu, Void arg) {
        cu.findAll(ClassOrInterfaceDeclaration.class).forEach(this::visitClass);
        cu.findAll(MethodDeclaration.class).forEach(this::visitMethod);
    }

    private void visitClass(ClassOrInterfaceDeclaration n) {
        Map<String, Object> classSchemas = new HashMap<>();
        classSchemas.put("type", "class");
        classSchemas.put("description", cleanJavadoc(n.getJavadoc().orElse(null)));
        
        Map<String, Object> methods = new HashMap<>();
        n.getMethods().forEach(method -> {
            if (!shouldSkipMethod(method)) {
                methods.put(method.getNameAsString(), getMethodSchema(method));
            }
        });
        
        classSchemas.put("methods", methods);
        classSchemas.put("code", n.toString());
        toolSchemas.put(n.getNameAsString(), classSchemas);
    }

    private void visitMethod(MethodDeclaration n) {
        if (shouldSkipMethod(n)) {
            return;
        }
        
        Map<String, Object> methodSchema = getMethodSchema(n);
        methodSchema.put("code", n.toString());
        toolSchemas.put(n.getNameAsString(), methodSchema);
    }

    private Map<String, Object> getMethodSchema(MethodDeclaration method) {
        Map<String, Object> schema = new HashMap<>();
        
        schema.put("type", method.isDefault() ? "async_function" : "function");
        schema.put("description", cleanJavadoc(method.getJavadoc().orElse(null)));
        schema.put("signature", getMethodSignature(method));
        schema.put("parameters", parseJavadocParameters(method.getJavadoc().orElse(null)));
        
        return schema;
    }

    private String getMethodSignature(MethodDeclaration method) {
        return method.getDeclarationAsString(false, false, true);
    }

    private boolean shouldSkipMethod(MethodDeclaration method) {
        String name = method.getNameAsString();
        return name.startsWith("_") && !name.equals("__init__");
    }

    private String cleanJavadoc(com.github.javaparser.javadoc.Javadoc javadoc) {
        if (javadoc == null) {
            return "";
        }
        return javadoc.getDescription().toText().replaceAll("\\s+", " ").trim();
    }

    private Map<String, String> parseJavadocParameters(com.github.javaparser.javadoc.Javadoc javadoc) {
        Map<String, String> params = new HashMap<>();
        if (javadoc == null) {
            return params;
        }
        
        // Parse @param and @return tags
        javadoc.getBlockTags().forEach(tag -> {
            String tagName = tag.getTagName();
            String content = tag.getContent().toText();
            
            if ("param".equals(tagName)) {
                String[] parts = content.split("\\s+", 2);
                if (parts.length == 2) {
                    params.put(parts[0], parts[1].trim());
                }
            } else if ("return".equals(tagName)) {
                params.put("return", content.trim());
            }
        });
        
        return params;
    }

    public Map<String, Map<String, Object>> getToolSchemas() {
        return toolSchemas;
    }
}

@interface Documentation {
    String value();
}
