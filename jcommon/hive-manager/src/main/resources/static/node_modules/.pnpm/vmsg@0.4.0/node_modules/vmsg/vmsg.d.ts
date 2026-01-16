declare module "vmsg" {
  interface RecordOptions {
    wasmURL?: string;
    shimURL?: string;
    pitch?: number;
  }

  export class Recorder {
    constructor(opts: RecordOptions);
    stopRecording(): Promise<Blob>;
    initAudio(): Promise<void>;
    initWorker(): Promise<void>;
    init(): Promise<void>;
    startRecording(): void;
    close(): void;
  }

  interface Exports {
    record: (opts?: RecordOptions) => Promise<Blob>;
  }
  const exports: Exports;
  export default exports;
}
