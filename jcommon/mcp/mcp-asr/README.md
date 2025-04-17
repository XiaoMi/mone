#### 本地IO模式配置
```
{
	"mcpServers": {
		"asr_mcp": {
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
        "asr_mcp": {
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

tencent.asr.appId=
tencent.asr.secretId=
tencent.asr.secretKey=
# 每次推送大小 byte
tencent.asr.speechLength=160
# 每次推送间隔时间 (ms)
tencent.asr.sleepTime=50
# 8k_zh,8k_en,8k_zh_large 
tencent.asr.engineModelType=8k_zh

ali.asr.appKey=
ali.asr.id=
ali.asr.secret=
ali.asr.url=wss://nls-gateway.aliyuncs.com/ws/v1
# 每次推流大小 byte
ali.asr.speechLength=1600
# 每次推送间隔时间 (ms)
ali.asr.sleepTime=50
# 音频采样率 16000 或者8000
ali.asr.sampleRate=16000

### 输入说明
提供音频地址和指定供应商，识别出其中的内容并输出

## 示例输入
使用腾讯云语音识别这个音频文件内容"C:\Users\mi\Tencent-pcm-8000-5c02f21c-4834-4668-a315-4664ab105d98.wav"

## 输出
腾讯语音识别结果为：您好，我是小米汽车智能客服，关于产品配件售价资产品订购、预约试驾及服务、门店位置咨询等问题，我可以帮您解答。请问有什么可以帮到您？

## 示例输入
使用阿里语音识别这个音频文件内容"C:\Users\mi\8010321-20250409185556-18839767171-2204--record-medias_12-1744196156.124350.mp3"

## 输出
阿里语音识别结果为：好的，先生。

```