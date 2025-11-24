package run.mone.mcp.cursor.miapi.parser;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import run.mone.mcp.cursor.miapi.model.ParserResult;
import run.mone.mcp.cursor.miapi.util.FileScanner;
import run.mone.mcp.cursor.miapi.util.ParseUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

/**
 * 基于源码解析的API解析器
 * 通过解析Java源码文件来提取接口信息
 */
public class SourceCodeApiParser {
    
    private static final Logger logger = LoggerFactory.getLogger(SourceCodeApiParser.class);
    private final JavaParser javaParser;

    private String codeRoot = "";

    private Integer batchSize = 20;
    
    public SourceCodeApiParser() {
        this(null);
    }
    
    public SourceCodeApiParser(String sourcePath) {
        codeRoot = sourcePath;
        // 配置SymbolResolver以支持类型解析
        CombinedTypeSolver typeSolver = new CombinedTypeSolver();
        typeSolver.add(new ReflectionTypeSolver());
        
        // 创建ParserConfiguration并设置SymbolResolver
        ParserConfiguration config = new ParserConfiguration();
        config.setSymbolResolver(new JavaSymbolSolver(typeSolver));
        
        this.javaParser = new JavaParser(config);
        logger.debug("SourceCodeApiParser初始化完成，已配置SymbolResolver");
    }
    
    /**
     * 解析目录下的所有Java文件
     * @param directoryPath 目录路径
     * @return 解析结果
     */
    public ParserResult parseDirectory(String directoryPath) {
        logger.info("开始解析目录: {}", directoryPath);
        ParserResult result = new ParserResult();
        result.setSuccess(true);
        
        try {
            List<String> javaFiles = FileScanner.scanJavaFiles(directoryPath);
            logger.info("找到 {} 个Java文件", javaFiles.size());
            
            if (javaFiles.isEmpty()) {
                logger.warn("目录下没有找到Java文件: {}", directoryPath);
                result.setSuccess(false);
                result.setErrorMessage("目录下没有找到Java文件: " + directoryPath);
                return result;
            }
            for (int i = 0; i < javaFiles.size(); i+=batchSize) {
                List<String> list = javaFiles.subList(i, Math.min(i + batchSize, javaFiles.size()));
                list.forEach(filePath-> {
                    logger.info("开始解析文件: {}", filePath);
                    try {
                        File file = new File(filePath);
                        if (file.exists()) {
                            CompilationUnit cu = javaParser.parse(file).getResult().orElse(null);
                            if (cu == null) {
                                logger.error("无法解析Java文件CompilationUnit: {}", filePath);
                            } else {
                                ParserResult fileResult = new ParserResult();
                                result.setSuccess(true);
                                ParseUtil.parseCompilationUnit(cu, fileResult, codeRoot);
                                ParseUtil.mergeResults(result, fileResult);
                            }
                        } else {
                            logger.error("文件不存在: {}", filePath);
                        }
                    } catch (FileNotFoundException e) {
                        logger.error("文件未找到: {}", filePath, e);
                    } catch (Exception e) {
                        logger.error("解析文件失败: {}", filePath, e);
                    }
                });
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    logger.error("解析失败: ", e);
                    break;
                }
            }
            // 处理相同地址的区分
            ParseUtil.processDuplicatePaths(result);
            logger.info("目录解析完成: {}, 总接口数: {}", directoryPath, result.getParsedApiCount());
        } catch (Exception e) {
            logger.error("解析目录失败: {}", directoryPath, e);
            result.setSuccess(false);
            result.setErrorMessage("解析目录失败: " + e.getMessage());
        } finally {
            ClassFieldExtractor.clearCache();
        }
        
        return result;
    }

}
