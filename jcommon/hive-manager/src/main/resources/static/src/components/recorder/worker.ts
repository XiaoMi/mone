import { initMp3MediaEncoder } from 'mp3-mediarecorder/worker'

initMp3MediaEncoder({ vmsgWasmUrl: `${import.meta.env.VITE_APP_WORKER_PATH}/scripts/vmsg/0.4.0/vmsg.wasm` })
