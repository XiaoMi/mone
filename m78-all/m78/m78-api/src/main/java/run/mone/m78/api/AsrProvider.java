package run.mone.m78.api;

import run.mone.m78.api.bo.multiModal.audio.OfflineAsrQueryReqDTO;
import run.mone.m78.api.bo.multiModal.audio.OfflineAsrQueryResDTO;
import run.mone.m78.api.bo.multiModal.audio.OfflineAsrReqDTO;
import run.mone.m78.api.bo.multiModal.audio.OfflineAsrResDTO;

public interface AsrProvider {
    OfflineAsrResDTO audioRecognizeOffline(OfflineAsrReqDTO req);

    OfflineAsrQueryResDTO queryAudioRecognizeRes(OfflineAsrQueryReqDTO req);
}
