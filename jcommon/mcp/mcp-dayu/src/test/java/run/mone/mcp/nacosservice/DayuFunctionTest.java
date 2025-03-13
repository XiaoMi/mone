package run.mone.mcp.nacosservice;

import com.google.gson.Gson;
import org.apache.dubbo.rpc.RpcContext;
import run.mone.hive.mcp.spec.McpSchema;
import run.mone.mcp.dayu.function.DayuFunction;

import java.util.Map;
import java.util.Properties;

/**
 * @author liuchuankang
 * @Type NacosServiceFunctionTest.java
 * @Desc
 * @date 2025/3/06 14:45
 */
public class DayuFunctionTest {
	public static void main(String[] args) {
		Properties properties = new Properties();
		properties.put("nacos.address","****");
		properties.put("dubbo.interface","com.xiaomi.dayu.api.service.DubboSearchService");
		properties.put("dubbo.group","staging");
		properties.put("dubbo.version","");
		DayuFunction dayuFunction = new DayuFunction();
		McpSchema.CallToolResult apply = dayuFunction.apply(Map.of("type", "query", "application", "dayu"));
		System.out.println(new Gson().toJson(apply.content().get(0)));
	}
}
