package run.mone.mcp.nacosservice;

import com.google.gson.Gson;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.mcp.nacosservice.function.NacosServiceFunction;

import java.util.Map;
import java.util.Properties;

/**
 * @author liuchuankang
 * @Type NacosServiceFunctionTest.java
 * @Desc
 * @date 2025/2/21 14:45
 */
public class NacosServiceFunctionTest {
	public static void main(String[] args) {
		Properties properties = new Properties();

		NacosServiceFunction nacosServiceFunction = new NacosServiceFunction(properties);
		McpSchema.CallToolResult apply = nacosServiceFunction.apply(Map.of("type", "query", "serviceName", "providers:*:staging"));
		System.out.println(new Gson().toJson(apply.content().get(0)));
	}
}
