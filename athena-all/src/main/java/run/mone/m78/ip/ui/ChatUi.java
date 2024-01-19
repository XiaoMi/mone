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

package run.mone.m78.ip.ui;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.event.DocumentEvent;
import com.intellij.openapi.editor.event.DocumentListener;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiFile;
import run.mone.m78.ip.bo.Response;
import run.mone.m78.ip.bo.UserVo;
import run.mone.m78.ip.common.ConfigUtils;
import run.mone.m78.ip.common.MessageQueue;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.awt.event.*;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;

/**
 * @author goodjava@qq.com
 */
public class ChatUi extends JDialog implements WindowListener {

    private boolean open = false;

    private static final Logger logger = Logger.getInstance(ChatUi.class);


    private JPanel contentPane;
    private JTextArea textArea1;
    private JTree tree1;
    private JTextArea textArea2;
    private AnActionEvent event;

    private volatile boolean stop = false;

    public ChatUi(AnActionEvent anActionEvent) {
        if (!open) {
            return;
        }

        this.event = anActionEvent;
        setContentPane(contentPane);
        setModal(true);

        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);


        new Thread(() -> {
            while (true) {
                final Response res = MessageQueue.ins().poll();
                if (null != res) {

                    if (res.getCmd().equals("$exit$")) {
                        break;
                    }

                    try {
                        SwingUtilities.invokeAndWait(() -> {

                            String cmd = res.getCmd();

                            if (cmd.equals("talk_message")) {
                                textArea2.append(res.getSenderId() + ":" + res.getData().toString());
                                return;
                            }

                            //登陆好友的信息
                            if (cmd.equals("login")) {
                                List<UserVo> list = (List<UserVo>) res.getData();
                                list.stream().forEach(u -> {
                                    addUser(u);
                                });
                                return;
                            }

                            if (cmd.equals("logout_msg")) {
                                String uid = res.getData().toString();
                                deleteUser(uid);
                                return;
                            }

                            //有新用户登陆
                            if (cmd.equals("login_user_info")) {
                                UserVo user = (UserVo) res.getData();
                                addUser(user);
                                return;
                            }

                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
            logger.info("chat ui stop");

        }).start();

        init();

        this.setSize(this.getWidth() + 1, this.getHeight() + 1);
        textArea1.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {

                if (e.getModifiers() == 2 || e.getKeyCode() == 10) {
                    sendMessage();
                }
            }
        });
    }

    /**
     * 发送消息
     *
     * @return
     */
    @NotNull
    private void sendMessage() {
        //发送消息
        Map<String, Object> m = new HashMap<>(2);
        m.put("cmd", "talk");
        String msg = textArea1.getText().trim() + "\r\n";
        m.put("message", msg);

        if (null != tree1.getSelectionPath()) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree1.getSelectionPath().getLastPathComponent();
            UserVo uv = (UserVo) node.getUserObject();
            String id = uv.getId();
            if (!id.equals("YouPin")) {
                m.put("receiverId", id);
            }

            //变为review代码(只有选中别人的时候可以是这种操作)
            if (msg.equals("$review$\r\n") && !id.equals("YouPin") && !id.equals("robot")) {
                m.put("cmd", "review");
                Project project = event.getProject();
                PsiFile psiFile = event.getData(LangDataKeys.PSI_FILE);
                if (null == psiFile) {
                    return;
                }
                Document document = PsiDocumentManager.getInstance(project).getDocument(psiFile);
                m.put("data", document.getText());
                m.put("name", psiFile.getName());

                final String _id = id;
                final String _name = psiFile.getName();

                //添加事件(修改就会触发)
                document.addDocumentListener(new DocumentListener() {
                    @Override
                    public void documentChanged(@NotNull DocumentEvent event) {
                        Map<String, Object> m = new HashMap<>();
                        m.put("cmd", "review");
                        m.put("data", event.getDocument().getText());
                        m.put("name", _name);
                        m.put("receiverId", _id);
                        MessageQueue.ins().send(m);
                    }
                });
            }

        }
        MessageQueue.ins().send(m);
        textArea1.setText("");
    }


    private void init() {
        if (StringUtils.isNotEmpty(ConfigUtils.getConfig().getNickName())) {
            Map<String, Object> msg = new HashMap<>(2);
            msg.put("cmd", "login");
            msg.put("id", ConfigUtils.getConfig().getNickName());
            msg.put("name", ConfigUtils.getConfig().getNickName());
            MessageQueue.ins().send(msg);
            System.out.println("init finish");
        } else {
            this.textArea1.setText("请先设置昵称");
        }
    }


    /**
     * 添加用户
     *
     * @param user
     */
    private void addUser(UserVo user) {
        DefaultTreeModel model = (DefaultTreeModel) tree1.getModel();
        DefaultMutableTreeNode root = (DefaultMutableTreeNode) tree1.getModel()
                .getRoot();
        DefaultMutableTreeNode child = new DefaultMutableTreeNode(user);
        model.insertNodeInto(child, root, root.getChildCount());
    }


    /**
     * 移除用户
     *
     * @param uid
     */
    private void deleteUser(String uid) {
        DefaultMutableTreeNode top = (DefaultMutableTreeNode) tree1.getModel()
                .getRoot();
        Optional<DefaultMutableTreeNode> op = IntStream.range(0, top.getChildCount()).mapToObj(i -> (DefaultMutableTreeNode) top.getChildAt(i)).filter(it -> {
                    UserVo vo = (UserVo) it.getUserObject();
                    return vo.getId().equals(uid);
                }

        ).findAny();

        if (op.isPresent()) {
            top.remove(op.get());
            this.tree1.updateUI();
        }
    }


    private void onCancel() {
        this.stop = true;
        dispose();
    }



    public static void main(String[] args) throws URISyntaxException {

//        client = new WsClient();
//        client.init(new URI("ws://127.0.0.1:8089/ws"), (msg) -> {
//            System.out.println("-------------->" + msg);
//        });
//        client.connect();
//
        ChatUi dialog = new ChatUi(null);
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }

    private void createUIComponents() {
        tree1 = new JTree();
        DefaultMutableTreeNode top = new DefaultMutableTreeNode(new UserVo("YouPin", "YouPIn"));
        DefaultMutableTreeNode dp = new DefaultMutableTreeNode(new UserVo("robot", "robot"));
        top.add(dp);

        DefaultTreeModel treeModel = new DefaultTreeModel(top);
        this.tree1.setModel(treeModel);
    }

    @Override
    public void windowOpened(WindowEvent e) {

    }

    @Override
    public void windowClosing(WindowEvent e) {
        Response<String> res = new Response<>(0, "", "", "$exit$");
        MessageQueue.ins().offer(res);
    }

    @Override
    public void windowClosed(WindowEvent e) {

    }

    @Override
    public void windowIconified(WindowEvent e) {

    }

    @Override
    public void windowDeiconified(WindowEvent e) {
    }

    @Override
    public void windowActivated(WindowEvent e) {

    }

    @Override
    public void windowDeactivated(WindowEvent e) {

    }
}
