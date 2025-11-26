export default function ({ url = "", onOpen, onMessage, onError, onFinish }) {
  if (!url) {
    console.error(new Error("EventSource url不能为空"));
    return null;
  }

  let socket: WebSocket | null = null;

  try {
    socket = new WebSocket(url);

    socket.onopen = () => {
      console.log("WebSocket connection established.");
      onOpen?.();
    };

    socket.onmessage = (event) => {
      const message = event.data;
      onMessage?.(message);
    };

    socket.onclose = () => {
      console.log("WebSocket connection closed.");
      onFinish?.();
    };

    socket.onerror = (error) => {
      console.error("WebSocket error:", error);
      onError?.();
    };
  } catch (error) {
    console.error("Failed to create WebSocket connection:", error);
  }

  return socket;
}
