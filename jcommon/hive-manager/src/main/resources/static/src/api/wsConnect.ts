import { wsUtil } from "./wsUtils";

const WS_HOST = '/api/manager/ws/agent/chat';

export function connectWebSocket(uuid: string, onOpen: () => void, onClose: () => void, onMessage: (data: string) => void) {
  return wsUtil(WS_HOST + "?clientId=" + uuid, onOpen, onClose, onMessage);
}