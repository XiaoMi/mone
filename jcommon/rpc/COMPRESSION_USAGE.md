# RPC 压缩功能使用说明

## 概述

RPC 框架现在支持在网络传输时对消息体进行压缩，使用 GZIP 压缩算法。这可以显著减少网络传输的数据量，特别是对于大型消息体。

## 功能特性

- **自动压缩/解压缩**: 通过 extFields 中的标识符自动处理
- **透明操作**: 编码器和解码器自动处理压缩逻辑
- **压缩算法**: 使用 JDK 内置的 GZIP 压缩
- **可选功能**: 默认不启用，需要显式开启

## 使用方法

### 启用压缩

在创建 RemotingCommand 时，调用 `enableCompression()` 方法启用压缩：

```java
// 创建请求命令
RemotingCommand request = RemotingCommand.createRequestCommand(100);
request.setBody("这是一段需要压缩的数据...".getBytes(StandardCharsets.UTF_8));

// 启用压缩
request.enableCompression();

// 发送请求
// ... 发送逻辑
```

### 创建带压缩的 GSON 请求

```java
MyRequestData data = new MyRequestData();
data.setContent("大量数据内容...");

RemotingCommand request = RemotingCommand.createGsonRequestCommand(100, data);
request.enableCompression();  // 启用压缩
```

### 创建带压缩的 MsgPack 请求

```java
MyRequestData data = new MyRequestData();
RemotingCommand request = RemotingCommand.createMsgPackRequest(100, data);
request.enableCompression();  // 启用压缩
```

### 创建带压缩的响应

```java
String responseData = "响应数据...";
RemotingCommand response = RemotingCommand.createResponseCommand(200, responseData);
response.enableCompression();  // 启用压缩
```

## 工作原理

### 编码过程（NettyEncoder）

1. 检查 RemotingCommand 是否启用了压缩（通过 `isCompressionEnabled()` 方法）
2. 如果启用且 body 不为空，使用 CompressionUtil.compress() 压缩数据
3. 将压缩后的数据写入到网络流中
4. 压缩标识保存在 extFields 中，会随着 header 一起传输

### 解码过程（NettyDecoder）

1. 接收网络数据并解码为 RemotingCommand
2. 检查 extFields 中是否有压缩标识
3. 如果有压缩标识且 body 不为空，使用 CompressionUtil.decompress() 解压缩数据
4. 将解压缩后的数据设置回 RemotingCommand 的 body

## API 说明

### RemotingCommand

```java
// 启用 GZIP 压缩
public void enableCompression()

// 检查是否启用了压缩
public boolean isCompressionEnabled()

// 获取压缩类型
public String getCompressionType()

// 压缩相关常量
public static final String COMPRESSION_KEY = "compression";
public static final String COMPRESSION_TYPE_GZIP = "gzip";
```

### CompressionUtil

```java
// 压缩字节数组
public static byte[] compress(byte[] data) throws IOException

// 解压缩字节数组
public static byte[] decompress(byte[] compressedData) throws IOException
```

## 性能考虑

### 何时使用压缩

**适合使用压缩的场景：**
- 消息体较大（建议 > 1KB）
- 数据重复率高（文本、JSON、XML 等）
- 网络带宽有限
- 对延迟不是特别敏感

**不适合使用压缩的场景：**
- 消息体很小（< 1KB）
- 数据已经压缩过（如图片、视频）
- 对延迟要求极高
- CPU 资源紧张

### 压缩效果

压缩效果取决于数据特性：
- **文本数据**: 压缩率通常可达 60-80%
- **JSON/XML**: 压缩率通常可达 70-90%
- **已压缩数据**: 压缩率很低，不建议使用
- **随机数据**: 压缩率很低，可能反而增大

## 注意事项

1. **版本兼容性**: 确保通信双方都支持压缩功能
2. **错误处理**: 压缩/解压缩失败会关闭连接
3. **性能开销**: 压缩和解压缩会消耗 CPU 资源
4. **日志记录**: 启用 DEBUG 级别日志可以看到压缩详情
5. **自动判断**: 系统不会自动判断是否需要压缩，需要手动启用

## 示例代码

### 完整的请求-响应示例

```java
// 客户端发送压缩请求
public void sendCompressedRequest() {
    // 创建请求数据
    RequestData data = new RequestData();
    data.setLargeContent(generateLargeContent());
    
    // 创建 RPC 命令
    RemotingCommand request = RemotingCommand.createGsonRequestCommand(
        RequestCode.QUERY, data
    );
    
    // 启用压缩
    request.enableCompression();
    
    // 发送请求
    RemotingCommand response = remotingClient.invokeSync(
        "127.0.0.1:8080", 
        request, 
        3000
    );
    
    // 响应会自动解压缩（如果服务端压缩了的话）
    String result = new String(response.getBody());
    System.out.println("Response: " + result);
}

// 服务端处理并返回压缩响应
public RemotingCommand handleRequest(RemotingCommand request) {
    // 请求数据会自动解压缩
    String requestBody = new String(request.getBody());
    
    // 处理业务逻辑
    String resultData = processBusinessLogic(requestBody);
    
    // 创建响应
    RemotingCommand response = RemotingCommand.createResponseCommand(
        ResponseCode.SUCCESS, 
        resultData
    );
    
    // 如果请求使用了压缩，响应也使用压缩
    if (request.isCompressionEnabled()) {
        response.enableCompression();
    }
    
    return response;
}
```

## 日志示例

启用 DEBUG 级别日志后，可以看到类似以下的日志输出：

```
DEBUG - Body compressed for opaque: 12345, original size: 10240, compressed size: 2048
DEBUG - Body decompressed for opaque: 12345, compressed size: 2048, decompressed size: 10240
DEBUG - Compressed data from 10240 bytes to 2048 bytes, compression ratio: 80.00%
```

## 故障排查

### 问题：压缩后数据反而变大

**原因**: 数据本身不可压缩（已压缩数据或随机数据）

**解决**: 对小消息或已压缩数据不要启用压缩

### 问题：解压缩失败

**原因**: 数据损坏或压缩标识不匹配

**解决**: 检查网络传输是否正常，确保两端都使用相同版本的代码

### 问题：性能下降

**原因**: 过度使用压缩，CPU 成为瓶颈

**解决**: 只对大消息启用压缩，评估压缩带来的收益
