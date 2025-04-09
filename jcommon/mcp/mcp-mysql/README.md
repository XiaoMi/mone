"database-mcp": {
"command": "java",
"args": [
"-jar",
"-Dmysql.db=test",
"-Dmysql.password=123456",
"/Users/zhangzhiyong/IdeaProjects/open/mone/jcommon/mcp/mcp-mysql/target/app.jar"
]
}

# grpc
"database-mcp": {
"type": "grpc",
"env": {
"host": "10.38.216.204",
"port": "9786"
}
}