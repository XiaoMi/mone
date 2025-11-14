# 功能
- 在DiffTool工具执行完成后，自动发送消息通知外部接口(其实是通知jetbrains中的一个server暴露的http接口，方法名这里可以酌情参考)
- 本功能的检查开关已实现，参考DiffTool中的isAutoDiffNotify方法,你直接使用即可
- 通知外部接口文档如下:

## HTTP API 接口文档

### 检查点 Diff 接口

通过 HTTP 接口触发显示检查点 Diff 窗口。

**接口地址**: `POST http://127.0.0.1:3458/tianye`

#### 1. 获取所有检查点列表

**请求参数**:
```json
{
  "cmd": "show_checkpoint_diff",
  "projectName": "your-project-name"
}
```

**响应示例**:
```json
{
  "code": 0,
  "message": "Checkpoint list retrieved successfully",
  "data": {
    "checkpointCount": 3,
    "checkpoints": [
      {
        "id": "1731484800000_功能开发完成",
        "description": "功能开发完成",
        "timestamp": 1731484800000,
        "commitHash": "a1b2c3d4e5f6...",
        "shortCommitHash": "a1b2c3d"
      }
    ]
  }
}
```

#### 2. 显示指定检查点的所有变更文件

**请求参数**:
```json
{
  "cmd": "show_checkpoint_diff",
  "projectName": "your-project-name",
  "checkpointId": "1731484800000_功能开发完成"
}
```

**响应示例**:
```json
{
  "code": 0,
  "message": "Showing diff for 5 files",
  "data": {
    "checkpointId": "1731484800000_功能开发完成",
    "changedFilesCount": 5,
    "changedFiles": [
      "src/main/java/com/example/Service.java",
      "src/main/java/com/example/Controller.java"
    ]
  }
}
```

**效果**: 自动在 IDEA 中依次打开所有变更文件的 Diff 窗口（每个窗口间隔 300ms）

#### 3. 显示指定文件的 Diff

**请求参数**:
```json
{
  "cmd": "show_checkpoint_diff",
  "projectName": "your-project-name",
  "checkpointId": "1731484800000_功能开发完成",
  "filePath": "src/main/java/com/example/Service.java"
}
```

**响应示例**:
```json
{
  "code": 0,
  "message": "Showing diff for file: src/main/java/com/example/Service.java",
  "data": {
    "checkpointId": "1731484800000_功能开发完成",
    "changedFilesCount": 1,
    "changedFiles": [
      "src/main/java/com/example/Service.java"
    ]
  }
}
```

**效果**: 在 IDEA 中打开指定文件的 Diff 窗口

#### 错误响应

```json
{
  "code": -1,
  "message": "Checkpoint not found: invalid_id"
}
```

#### 使用示例

```bash
# 1. 获取所有检查点
curl -X POST \
  -H "Content-Type: application/json" \
  -d '{"cmd":"show_checkpoint_diff","projectName":"ultraman"}' \
  http://127.0.0.1:3458/tianye

# 2. 显示指定检查点的所有变更
curl -X POST \
  -H "Content-Type: application/json" \
  -d '{"cmd":"show_checkpoint_diff","projectName":"ultraman","checkpointId":"1731484800000_功能开发完成"}' \
  http://127.0.0.1:3458/tianye

# 3. 显示指定文件的 Diff
curl -X POST \
  -H "Content-Type: application/json" \
  -d '{"cmd":"show_checkpoint_diff","projectName":"ultraman","checkpointId":"1731484800000_功能开发完成","filePath":"src/main/java/run/mone/ultraman/HttpServerHandler.java"}' \
  http://127.0.0.1:3458/tianye
```

#### 注意事项

1. **projectName**: 必须是当前 IDEA 中已打开的项目名称
2. **checkpointId**: 可以先调用获取列表接口获取所有可用的检查点ID
3. **filePath**: 必须是相对于项目根目录的路径
4. Diff 窗口左侧显示检查点中的文件内容，右侧显示当前文件内容
5. 可以在 Diff 窗口中交互式地应用或放弃修改