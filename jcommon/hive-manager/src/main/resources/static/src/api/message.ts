import axios from 'axios';

interface McpRequest {
  outerTag: string;
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
    let preIndex = -1;
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
          xhr?.responseText.split('data:').forEach((chunk: string, index: number) => {
            if (index > preIndex) {
              callback?.(chunk);
              preIndex = index;
            }
          });
        }
      }
    });

    return response;
  } catch (error) {
    console.error('请求错误:', error);
    throw error;
  }
};
