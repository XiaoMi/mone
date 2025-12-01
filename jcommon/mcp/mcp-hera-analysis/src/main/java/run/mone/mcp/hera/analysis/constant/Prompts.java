package run.mone.mcp.hera.analysis.constant;

/**
 * AI分析相关的提示词常量
 *
 * @author dingtao
 */
public class Prompts {

    /**
     * 代码修复分析提示词
     * 用于分析trace链路，找出异常根因节点的spanId
     */
    public static final String CODE_FIX_ANALYSIS_PROMPT = """
            作为一位经验丰富的Java开发工程师，你负责分析请求链路中各个节点的Span信息，找出导致异常的根本原因节点的spanId。我们会提供一个包含请求链路全部节点的数据集。你的任务是：

            1. 确认服务之间的调用关系和依赖，从而确定每个节点在链路中的依赖层级。可以通过节点中的 references.spanID 来确认当前节点的父节点。
            2. 识别存在的异常节点，并找到最底层的异常节点。
            3. 如果 span.kind 是 client 节点，并且报服务调用超时异常，请判断其调用的 server 节点的耗时，若 server 节点耗时过高，则根本原因是 server 节点而非 client 节点。
            4. 返回异常根因节点的 spanId。

            请使用以下 XML 格式返回分析结果：
            ```xml
            <result>
              <spanId>异常根因节点的spanId</spanId>
            </result>
            ```

            请确保结果为有效的 XML 数据。如果传入的 data 数据为空，则返回以下固定的内容：
            ```xml
            <result>
              <spanId></spanId>
            </result>
            ```

            ## 返回结果示例：
            ```xml
            <result>
              <spanId>a291ab32156dca70</spanId>
            </result>
            ```

            以下是提供的数据集：
            """;
}
