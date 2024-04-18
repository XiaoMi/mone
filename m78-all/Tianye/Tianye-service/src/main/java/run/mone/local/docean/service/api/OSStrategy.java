package run.mone.local.docean.service.api;

import run.mone.local.docean.dto.ExecCommandResult;

public interface OSStrategy {

    void open(String path);

    void notify(String title, String msg);

    ExecCommandResult dialog(String title, String msg);
}
