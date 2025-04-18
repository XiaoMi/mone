#### 本地IO模式配置
```
{
	"mcpServers": {
		"tts_mcp": {
			"command": "C:\\Program Files\\Java\\jdk-21\\bin\\java.exe",
			"args": [
				"-jar",
				"C:\\Users\\mi\\Desktop\\app.jar"
			]
		}
	}
}
```

#### sse模式启动及启动命令
```
{
  "mcpServers": {
    "tts_mcp": {
      "type": "sse",
      "sseRemote": true,
      "url": "http://localhost:8080"
    }
  }
}

// 本地windows环境启动sse服务
"C:\\Program Files\\Java\\jdk-21\\bin\\java.exe" -jar C:\\Users\\mi\\Desktop\\app.jar

```

#### 使用示例
```
### 配置打包
需要先在properties配置种配置参数，打包后，会生成一个jar包，在jar包目录下，运行jar包即可

配置参数如下：
tencent.tts.appId=
tencent.tts.secretId=
tencent.tts.secretKey=
# 音色类型https://cloud.tencent.com/document/product/1073/92668#0d58314f-d302-4a01-91da-17b456aca73e
tencent.tts.voiceType=301032
# 音频采样率 8000,16000,24000
tencent.tts.sampleRate=8000
# 生成的音频格式：pcm(二进制为pcm流，生成的文件为wav格式), mp3
tencent.tts.codec=pcm

ali.tts.appKey=
ali.tts.id=
ali.tts.secret=
ali.tts.url=wss://nls-gateway.aliyuncs.com/ws/v1
# 音色类型https://help.aliyun.com/zh/isi/developer-reference/natual-tts-product-introduction?spm=a2c4g.11186623.help-menu-30413.d_3_1_1_0.1e6f6d33qKKqQg&scm=20140722.H_2767291._.OR_help-T_cn~zh-V_1#918f408a63o0n
ali.tts.voice=zhistella
# 音频采样率 8000,16000,24000
ali.tts.sampleRate=16000
# pcm,wav,mp3
ali.tts.outputFormat=pcm

### 输入说明
指定供应商和输入文本，会识别出其中的内容，输出二进制流，并且支持生成音频文件和在线播放，生成的音频文件目录为运行jar包目录

如: 在C:\Users\mi>  下执行
 "C:\\Program Files\\Java\\jdk-21\\bin\\java.exe" -jar C:\\Users\\mi\\Desktop\\app.jar
 启动命令，则生成的音频文件为
 C:\Users\mi\xxxxx.wav

## 示例输入
使用腾讯语音合成处理以下文本内容并播放：“好的，先生”

## 输出
腾讯语音合成pcm音频二进制流结果为：0, 0, 0, 0, 0, 0.........

## 示例输入
使用腾讯语音合成处理以下文本内容并播放和生成文件：“好的，先生”

## 输出
腾讯语音合成pcm音频二进制流结果为：0, 0, 0, 0, 0, 0.........

## 示例输入
使用阿里语音合成处理以下文本内容：“先生，你好。”

## 输出
阿里语音合成pcm音频二进制流结果为：0, 0, 0, 0, 0, 0.........
```