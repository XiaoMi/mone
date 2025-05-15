import axios from 'axios';
import { ElMessage } from 'element-plus';

interface McpRequest {
  outerTag: string;
  mapData: any;
  content: {
    server_name: string;
    tool_name: string;
    arguments: string;
  }
}

export const streamChat = async (params: any, callback: (data: any) => void) => {
  const request: McpRequest = {
    // outerTag: "use_mcp_tool",
    // content: {
    //   server_name: "chat-mcp",
    //   tool_name: "stream_minzai_chat",
    //   arguments: JSON.stringify({ message, clientId, __owner_id__: clientId })
    // }
    ...params
  };

  try {
    // let preIndex = -1;
    let text = '';
    const response = await axios({
      method: 'post',
      url: '/api/manager/v1/mcp/call',
      data: request,
      responseType: 'stream',
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`,
        'Accept': 'text/event-stream',
        'Content-Type': 'text/event-stream'
      },
      onDownloadProgress: (progressEvent) => {
        const xhr = progressEvent.event?.target;
        if (xhr?.responseText) {
          xhr?.responseText.replace('data: ', '');
          let allText = '';
          if (xhr?.responseText.indexOf("predictions") > -1 || xhr?.responseText.indexOf("[DONE]") > -1) {
            if (xhr?.responseText.indexOf("[DONE]") > -1){
              for (const chunk of xhr?.responseText.split('data:')) {
                if (chunk.indexOf("predictions") > -1) {
                  callback?.(chunk);
                  break;
                }
              }
            }
          } else {
            xhr?.responseText.split('data:').forEach((chunk: string, index: number) => {
              if (chunk.includes("hiveVoiceBase64-")) {
                textToVoice(chunk.split("hiveVoiceBase64-")[1]);
              }else {
                // -2需要去掉最后一个\n
                allText += chunk.slice(0, -2);
              }
            });
            // console.log(allText);
            // 新增的部分
            const resText = allText.slice(text.length);
            // console.log(resText);
            text = allText;
            if (resText) {
              callback?.(resText);
            }
          }
        }
      }
    });

    return response;
  } catch (error) {
    console.error('请求错误:', error);
    throw error;
  }
};

export const textToVoice = async (text: string) => {
  try {
    if (text) {
      const audio = new Audio(`data:audio/mpeg;base64,${text}`);
      await audio.play();
    }
  } catch (error) {
    console.log("error", error);
    ElMessage.error('音频播放失败')
  }
}