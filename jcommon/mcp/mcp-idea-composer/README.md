+ 可以操控本地idea的mcp
+ 支持功能
  + composer功能

"database-idea-composer": {
"command": "java",
"args": [
"-jar",
"-Didea.port=30000",
"/Users/ericgreen/mycode/mone/jcommon/mcp/macp-idea-composer/target/app.jar"
],
"env": {
"IDEA_PORT": "30000"
}
}