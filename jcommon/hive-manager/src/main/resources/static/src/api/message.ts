import axios from 'axios';

interface McpRequest {
  outerTag: string;
  content: {
    server_name: string;
    tool_name: string;
    arguments: string;
  }
}

export const streamChat = async (message: string, callback: (data: any) => void) => {
  const request: McpRequest = {
    outerTag: "use_mcp_tool",
    content: {
      server_name: "chat-mcp",
      tool_name: "stream_minzai_chat",
      arguments: JSON.stringify({ message })
    }
  };

  try {
    const response = await axios({
      method: 'post',
      url: '/api/manager/v1/mcp/call',
      data: request,
      responseType: 'text',
      headers: {
        'Authorization': `Bearer ${localStorage.getItem('token')}`,
        'Accept': 'text/event-stream',
        'Content-Type': 'text/event-stream'
      },
      onDownloadProgress: (progressEvent) => {
        console.log("progressEvent", progressEvent.event?.target?.response)
        callback?.(progressEvent.event?.target?.response)
      }
    });

    return response;
  } catch (error) {
    console.error('请求错误:', error);
    throw error;
  }
};
