// import { useChatContextStore } from "@/stores/chat-context";
import type { AiMessage } from ".";

// 添加一个变量来跟踪当前播放的音频
let currentAudio: HTMLAudioElement | null = null;

export function soundHandler (data: AiMessage, user: { username: string; cname: string; avatar: string; roles: string[]; }) {
  const base64 = data.message;
  if (base64) {
    // 如果有正在播放的音频，先停止并释放资源
    if (currentAudio) {
      currentAudio.pause();
      currentAudio.currentTime = 0;
      currentAudio = null;
    }

    // 创建新的音频并播放
    const audio = new Audio("data:audio/mpeg;base64," + base64);
    currentAudio = audio;
    audio.play().catch(error => {
      console.warn('自动播放失败:', error);
      currentAudio = null;
    });
  }
}
