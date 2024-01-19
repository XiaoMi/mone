package run.mone.m78.ip.dialog;

import com.intellij.openapi.application.ApplicationInfo;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.ui.jcef.JBCefApp;
import com.intellij.ui.jcef.JBCefBrowser;
import com.intellij.ui.jcef.JBCefClient;
import run.mone.m78.ip.listener.ChromeMessageRouterHandler;
import run.mone.m78.ip.util.ScreenSizeUtils;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.browser.CefMessageRouter;
import org.cef.callback.CefQueryCallback;
import org.cef.handler.CefMessageRouterHandlerAdapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;


/**
 * @author caobaoyu
 * @description:
 * @date 2023-06-13 16:42
 */
public class ChromeDialog extends DialogWrapper {

    private String url;

    public static Map<String, JBCefBrowser> browserMap = new HashMap<>();

    private Project project;

    private boolean hasHttpRequest;

    public ChromeDialog(String url, Project project) {
        super(true);
        this.url = url;
        this.project = project;
        this.hasHttpRequest = false;
        init();
        setTitle("Chrome Browser");
    }


    @Override
    protected @Nullable JComponent createCenterPanel() {
        JBCefBrowser browser = null;
        JPanel webPanel = new JPanel();
        if (JBCefApp.isSupported()) {
            if (System.getProperty("os.name").contains("Linux")) {
                browser = JBCefBrowser.createBuilder().setOffScreenRendering(true).build();
            } else if (ApplicationInfo.getInstance().getBuild().asStringWithoutProductCode().startsWith("23")) {
                browser = JBCefBrowser.createBuilder().setOffScreenRendering(false).build();
            } else {
                browser = new JBCefBrowser();
            }
        }
        browser.loadURL(url);
        browserMap.put(this.project.getName(),browser);
        @NotNull JBCefClient client = browser.getJBCefClient();

        //右键菜单
        CefMessageRouter cmr = CefMessageRouter.create(new CefMessageRouter.CefMessageRouterConfig("cef", "cefCancel"));
        cmr.addHandler(new CefMessageRouterHandlerAdapter() {
            @Override
            public boolean onQuery(CefBrowser browser, CefFrame frame, long queryId, String request, boolean persistent, CefQueryCallback callback) {
                if (request.indexOf("click:") == 0) {
                    hasHttpRequest = true;
                    closeDialog();
                }
                // 返回false表示不拦截该请求，继续传递给其他处理器处理
                return false;
            }
        }, true);
        cmr.addHandler(new ChromeMessageRouterHandler(project), false);
        client.getCefClient().addMessageRouter(cmr);

        webPanel.add(browser.getComponent(), BorderLayout.CENTER);
        webPanel.setSize(ScreenSizeUtils.size());
        webPanel.setVisible(true);
        return webPanel;
    }

    @Override
    protected Action[] createActions() {
        return new Action[0];
    }

    private void closeDialog() {
        SwingUtilities.invokeLater(() -> {
            ApplicationManager.getApplication().invokeLater(() -> {
                close(DialogWrapper.OK_EXIT_CODE);
            });
        });
    }

}
