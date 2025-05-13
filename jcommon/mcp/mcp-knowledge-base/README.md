#### GRPC模式配置
```
"knowledge_base_query-mcp": {
    "type": "grpc",
    "env": {
      "host": "127.0.0.1",
      "port": "9786"
    }
  }
```

#### 本地IO模式配置
```
{
	"mcpServers": {
		"knowledge_base_query": {
			"command": "C:\\Program Files\\Java\\jdk-21\\bin\\java.exe",
			"args": [
				"-jar",
				"C:\\Users\\mi\\Desktop\\app.jar"
			],
			"env": {
				"API_HOST": "http://127.0.0.1:8083"
			}
		}
	}
}
```


#### sse模式启动及启动命令
```
{
	"mcpServers": {
		"knowledge_base_query": {
            "type": "sse",
            "sseRemote": true,
            "url": "http://localhost:8080",
			"env": {
				"API_HOST": "http://127.0.0.1:8083"
			}
		}
	}
}

// 本地windows环境启动sse服务
set API_HOST=http://127.0.0.1:8083
"C:\\Program Files\\Java\\jdk-21\\bin\\java.exe" -jar C:\\Users\\mi\\Desktop\\app.jar

```

#### 使用示例
```
## 示例输出
帮我查下小米汽车北京大兴区附近的服务中心有哪些？

## 输出
<attempt_completion>
<result>
The following Xiaomi car service centers are in Wuhan:

*   小米汽车湖北省武汉市江岸区黄浦科技园销售服务中心
*   小米汽车武汉市东西湖区东西湖团结街授权服务中心

</result>
</attempt_completion>
```