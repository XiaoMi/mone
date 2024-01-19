/*
 *  Copyright 2020 Xiaomi
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package run.mone.m78.ip.listener;

import com.google.gson.Gson;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.jcef.JBCefApp;
import com.intellij.ui.jcef.JBCefBrowser;
import com.intellij.ui.jcef.JBCefClient;
import run.mone.m78.ip.renderer.CustomIconRenderer;
import run.mone.m78.ip.util.ProjectUtils;
import run.mone.m78.ip.bo.SpiderUrl;
import run.mone.m78.ip.bo.TbTask;
import run.mone.m78.ip.common.ApiCall;
import run.mone.m78.ip.common.ConfigUtils;
import run.mone.m78.ip.service.SpiderService;
import run.mone.m78.ip.service.TaskService;
import run.mone.m78.ip.service.UserService;
import run.mone.m78.ip.util.FileUtils;
import run.mone.m78.ip.util.UltramanConsole;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.browser.CefMessageRouter;
import org.cef.callback.CefContextMenuParams;
import org.cef.callback.CefMediaAccessCallback;
import org.cef.callback.CefMenuModel;
import org.cef.handler.CefContextMenuHandlerAdapter;
import org.cef.handler.CefLifeSpanHandlerAdapter;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Author goodjava@qq.com
 * @Date 2021/11/11 11:33
 */
@Data
@Slf4j
public class UltrmanTreeKeyAdapter extends KeyAdapter {

    private Gson gson = new Gson();

    private JTextField textField1;
    private JTree tree1;
    private JPanel webPannel;
    private JPanel treePannel;
    private int MENU_ID_SHOW_DEV_TOOLS = 288;

    private Project project;

    private JBCefBrowser browser;

    private JBCefClient client;

    public static Map<String, JBCefBrowser> browserMap = new ConcurrentHashMap<>();

    public UltrmanTreeKeyAdapter(Project project, JTextField textField1, JTree tree1, JPanel webPannel, JPanel treePannel) {
        this.project = project;
        this.textField1 = textField1;
        this.tree1 = tree1;
        this.webPannel = webPannel;
        this.treePannel = treePannel;
    }

    public void loadMone(String apiUrl, String text) {
        if (text.startsWith("mone")) {
            String[] ss = text.split(":");
            if (JBCefApp.isSupported()) {
                //需要退出老浏览器
                if (null != this.browser) {
                    this.client.dispose();
                    this.browser.dispose();
                    this.webPannel.remove(this.browser.getComponent());
                    log.info("quit browser");
                }

                this.treePannel.setVisible(false);
                if (System.getProperty("os.name").contains("Linux")) {
                    browser = JBCefBrowser.createBuilder().setOffScreenRendering(true).build();
                } else {
                    browser = JBCefBrowser.createBuilder().setOffScreenRendering(false).build();
                }
                this.webPannel.add(browser.getComponent(), BorderLayout.CENTER);
                String url = ss.length > 1 ? apiUrl + "?v=1" : apiUrl;
                browser.loadURL(url);

                browserMap.put(this.project.getName(), browser);

                client = browser.getJBCefClient();

                //允许录音
                client.getCefClient().addPermissionHandler((browser, frame, requesting_url, requested_permissions, callback) -> {
                    callback.Continue(CefMediaAccessCallback.MediaPermissionFlags.DEVICE_AUDIO_CAPTURE);
                    return true;
                });

                client.addLifeSpanHandler(new CefLifeSpanHandlerAdapter() {
                    @Override
                    public boolean onBeforePopup(CefBrowser browser, CefFrame frame, String target_url, String target_frame_name) {
                        //打开新的链接
                        log.info("on before popup:" + browser.getURL() + "," + target_url);
                        return super.onBeforePopup(browser, frame, target_url, target_frame_name);
                    }
                }, browser.getCefBrowser());

                //右键菜单
                client.addContextMenuHandler(new CefContextMenuHandlerAdapter() {
                    @Override
                    public void onBeforeContextMenu(CefBrowser browser, CefFrame frame, CefContextMenuParams params, CefMenuModel model) {
                        model.clear();
                        model.addItem(CefMenuModel.MenuId.MENU_ID_COPY, "copy");
                        model.addItem(CefMenuModel.MenuId.MENU_ID_CUT, "cut");
                        model.addItem(CefMenuModel.MenuId.MENU_ID_PASTE, "paste");
                        model.setEnabled(CefMenuModel.MenuId.MENU_ID_PASTE, true);
                        model.addSeparator();
                        model.addItem(CefMenuModel.MenuId.MENU_ID_RELOAD, "reload");
                        model.addItem(MENU_ID_SHOW_DEV_TOOLS, "Developer options");
                    }

                    @Override
                    public boolean onContextMenuCommand(CefBrowser browser, CefFrame frame, CefContextMenuParams params, int commandId, int eventFlags) {

                        switch (commandId) {
                            case 288:
                                // 打开开发者选项
                                DevToolsDialog devToolsDlg = new DevToolsDialog(null, "Developer options", browser);
                                devToolsDlg.setVisible(true);
                                return true;
                        }
                        return false;
                    }
                }, browser.getCefBrowser());


                CefMessageRouter cmr = CefMessageRouter.create(new CefMessageRouter.CefMessageRouterConfig("cef", "cefCancel"));
                cmr.addHandler(new ChromeMessageRouterHandler(project), true);
                client.getCefClient().addMessageRouter(cmr);

                this.webPannel.setVisible(true);
            }
        }
    }

    @Override
    public void keyPressed(KeyEvent keyEvent) {
        if (keyEvent.getKeyCode() == KeyEvent.VK_ENTER) {
            String text = textField1.getText();
            ApiCall apiCall = new ApiCall();
            this.webPannel.setVisible(false);
            this.treePannel.setVisible(true);

            String apiUrl = ConfigUtils.getConfig().getDashServer();

            if (text.startsWith("mone")) {
                loadMone(apiUrl, text);
                return;
            }

            if (text.equals("music")) {
                List<String> list = apiCall.call(ApiCall.MUSIC_API);
                setTreeModel("music", list);
            }

            if (text.equals("image")) {
                List<String> list = apiCall.call(ApiCall.IMAGE_API);
                setTreeModel("image", list);
            }

            if (text.equals("text")) {
                List<String> list = apiCall.call(ApiCall.TEXT_API);
                setTreeModel("text", list);
            }

            if (text.equals("user")) {
                UserService userService = new UserService();
                List<String> list = userService.users();
                setTreeModel("user", list);
            }

            if (text.equals("task")) {
                TaskService taskService = new TaskService();
                List<TbTask> list = taskService.tasks(ConfigUtils.user()).stream().map(it -> {
                    TbTask task = gson.fromJson(it, TbTask.class);
                    return task;
                }).collect(Collectors.toList());
                setTreeModel("task", list, it -> it.getContent());
            }


            if (text.startsWith("spider:")) {
                String[] ss = text.split(":");
                SpiderService service = new SpiderService();
                List<SpiderUrl> list = service.list(ss[1]);
                setTreeModel("spider", list, (it) -> it.getTitle());
            }

            //文件上传和下载(目前有bug)
            if (text.startsWith("&&&file:")) {
                String[] ss = text.split(":");
                String cmd = ss[1];
                String name = ss[2];

                if (cmd.equals("download")) {
                    FileUtils.download(name, "/tmp/");
                    UltramanConsole.append("download " + name + " success");
                }

                if (cmd.equals("upload")) {
                    try {
                        FileChooserDescriptor chooserDescriptor = new FileChooserDescriptor(true, true, true, true, true, true);
                        VirtualFile virtualFile = FileChooser.chooseFile(chooserDescriptor, ProjectUtils.project(), null);
                        if (null != virtualFile) {
                            String path = virtualFile.getPath();
                            FileUtils.upload(name, path);
                            UltramanConsole.append("upload " + name + " success");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    }


    private void setTreeModel(String name, List<String> list) {
        setTreeModel(name, list, (str) -> str);
    }

    private <T> void setTreeModel(String name, List<T> list, Function<T, String> function) {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("root");
        list.forEach(it -> {
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(function.apply(it));
            node.setUserObject(it);
            root.add(node);
        });
        DefaultTreeModel treeModel = new DefaultTreeModel(root);
        tree1.setModel(treeModel);
        tree1.setCellRenderer(new CustomIconRenderer(name));
    }

}
