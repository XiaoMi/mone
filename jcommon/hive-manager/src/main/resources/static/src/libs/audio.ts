export function encodeWAV(pcmBase64: string, sampleRate = 24000, numChannels = 1) {
  // 解码 base64
  const binary = atob(pcmBase64);
  const pcmData = new Uint8Array(binary.length);
  for (let i = 0; i < binary.length; i++) {
    pcmData[i] = binary.charCodeAt(i);
  }

  const buffer = new ArrayBuffer(44 + pcmData.length);
  const view = new DataView(buffer);

  function writeString(view: DataView<ArrayBuffer>, offset: number, string: string) {
    for (let i = 0; i < string.length; i++) {
      view.setUint8(offset + i, string.charCodeAt(i));
    }
  }

  writeString(view, 0, 'RIFF');
  view.setUint32(4, 36 + pcmData.length, true);
  writeString(view, 8, 'WAVE');
  writeString(view, 12, 'fmt ');
  view.setUint32(16, 16, true); // Subchunk1Size
  view.setUint16(20, 1, true);  // AudioFormat (PCM)
  view.setUint16(22, numChannels, true);
  view.setUint32(24, sampleRate, true);
  view.setUint32(28, sampleRate * numChannels * 2, true); // ByteRate
  view.setUint16(32, numChannels * 2, true); // BlockAlign
  view.setUint16(34, 16, true); // BitsPerSample
  writeString(view, 36, 'data');
  view.setUint32(40, pcmData.length, true);

  // PCM 数据（16bit）
  for (let i = 0; i < pcmData.length; i += 2) {
    // 低字节在前，高字节在后（小端）
    const sample = pcmData[i] | (pcmData[i + 1] << 8);
    view.setInt16(44 + i, sample, true);
  }

  const wavBlob = new Blob([view], { type: 'audio/wav' });
  return new Promise((resolve) => {
    const reader = new FileReader();
    reader.onloadend = () => {
      const wavBase64 = (reader.result as string).split(',')[1];
      resolve(wavBase64);
    };
    reader.readAsDataURL(wavBlob);
  });
}

/**
 * 拼接多个 base64 PCM 音频片段并编码为 WAV（base64）
 * @param pcmBase64List base64 PCM 音频片段数组
 * @param sampleRate 采样率，默认24000
 * @param numChannels 通道数，默认1
 * @returns Promise<string> 拼接后的WAV的base64
 */
export function concatAndEncodeWAV(pcmBase64List: string[], sampleRate = 24000, numChannels = 1) {
  // 解码所有base64 PCM片段并拼接
  let totalLength = 0;
  const pcmBuffers: Uint8Array[] = pcmBase64List.map(base64 => {
    const binary = atob(base64);
    const pcm = new Uint8Array(binary.length);
    for (let i = 0; i < binary.length; i++) {
      pcm[i] = binary.charCodeAt(i);
    }
    totalLength += pcm.length;
    return pcm;
  });
  // 合并所有PCM数据
  const mergedPCM = new Uint8Array(totalLength);
  let offset = 0;
  for (const buf of pcmBuffers) {
    mergedPCM.set(buf, offset);
    offset += buf.length;
  }

  // 生成WAV
  const buffer = new ArrayBuffer(44 + mergedPCM.length);
  const view = new DataView(buffer);

  function writeString(view: DataView<ArrayBuffer>, offset: number, string: string) {
    for (let i = 0; i < string.length; i++) {
      view.setUint8(offset + i, string.charCodeAt(i));
    }
  }

  writeString(view, 0, 'RIFF');
  view.setUint32(4, 36 + mergedPCM.length, true);
  writeString(view, 8, 'WAVE');
  writeString(view, 12, 'fmt ');
  view.setUint32(16, 16, true); // Subchunk1Size
  view.setUint16(20, 1, true);  // AudioFormat (PCM)
  view.setUint16(22, numChannels, true);
  view.setUint32(24, sampleRate, true);
  view.setUint32(28, sampleRate * numChannels * 2, true); // ByteRate
  view.setUint16(32, numChannels * 2, true); // BlockAlign
  view.setUint16(34, 16, true); // BitsPerSample
  writeString(view, 36, 'data');
  view.setUint32(40, mergedPCM.length, true);

  // PCM 数据（16bit）
  for (let i = 0; i < mergedPCM.length; i += 2) {
    const sample = mergedPCM[i] | (mergedPCM[i + 1] << 8);
    view.setInt16(44 + i, sample, true);
  }

  const wavBlob = new Blob([view], { type: 'audio/wav' });
  return new Promise<string>((resolve) => {
    const reader = new FileReader();
    reader.onloadend = () => {
      const wavBase64 = (reader.result as string).split(',')[1];
      resolve(wavBase64);
    };
    reader.readAsDataURL(wavBlob);
  });
}
