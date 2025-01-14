package run.mone.hive.mcp.spec;

import run.mone.hive.mcp.spec.McpSchema.JSONRPCResponse.JSONRPCError;

/**
 * ORIGINAL CODE IS FROM SPRING AI!!!
 * 
 */
public class McpError extends RuntimeException {

	private JSONRPCError jsonRpcError;

	public McpError(JSONRPCError jsonRpcError) {
		super(jsonRpcError.message());
		this.jsonRpcError = jsonRpcError;
	}

	public McpError(Object error) {
		super(error.toString());
	}

	public JSONRPCError getJsonRpcError() {
		return jsonRpcError;
	}

}