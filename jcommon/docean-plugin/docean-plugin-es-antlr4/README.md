支持大多数kibana语法,只需要写语法即可生成SearchRequest去查询，具体可当单元测试

支持多种语法,使用括号可以支持优先级
需要需要新加语法或者减少语法，可以直接修改g4包下[EsQuery.g4]文件，然后在idea中
右击 Generate Antlr Recongnizer重新生成生成语法类（在query包下）
直接使用[EsQueryUtils.java]使用