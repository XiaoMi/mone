#!/bin/bash

# 检查是否提供了至少一个参数
if [ "$#" -eq 0 ]; then
    echo "未提供额外的启动参数!"
    #exit 1
fi

# 定义JAR包的路径和名称
JAR_PATH="./Tianye-server/target/Tianye-server-1.0.0-SNAPSHOT.jar"

# 构建Java命令，包含-D参数和脚本参数
JAVA_CMD="java -jar "

# 遍历脚本参数，并将其添加到JAVA_CMD中
for arg in "$@"; do
    JAVA_CMD="$JAVA_CMD -D$arg"
done

# 添加默认参数
JAVA_CMD="$JAVA_CMD --add-opens java.base/jdk.internal.misc=ALL-UNNAMED -Dio.netty.tryReflectionSetAccessible=true --add-opens=java.base/java.lang=ALL-UNNAMED --add-opens=java.base/java.util=ALL-UNNAMED --add-opens=java.base/java.math=ALL-UNNAMED --add-opens=java.base/sun.reflect=ALL-UNNAMED --add-exports=java.base/sun.reflect.annotation=ALL-UNNAMED --add-exports=java.base/sun.reflect.generics.reflectiveObjects=ALL-UNNAMED --enable-preview"

# 添加jar
JAVA_CMD="$JAVA_CMD $JAR_PATH"

# 执行Java命令
echo "启动Agent，使用以下参数: $JAVA_CMD"
$JAVA_CMD