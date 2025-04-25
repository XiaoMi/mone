import { wsUtil } from "./wsUtils";

const WS_HOST = '/api/manager/ws/agent/chat';

export function connectWebSocket(uuid: string,onClose: () => void, onMessage: (data: any) => void) {
    return wsUtil(WS_HOST + "?clientId=" + uuid,() => {},onClose, onMessage);
}