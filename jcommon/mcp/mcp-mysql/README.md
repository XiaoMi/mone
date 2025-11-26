"database-mcp": {
"command": "java",
"args": [
"-jar",
"-Dmysql.db=test",
"-Dmysql.password=123456",
"/Users/zhangzhiyong/IdeaProjects/open/mone/jcommon/mcp/mcp-mysql/target/app.jar"
]
}

"database-mcp": {
"type": "grpc",
"env": {
"host": "127.0.0.1",
"port": "9786"
}
}


//数据库管理的mcp 支持sqlite 和 mysql