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

package run.mone.m78.ip.component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.intellij.AppTopics;
import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.event.*;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import com.intellij.psi.PsiMethod;
import com.intellij.psi.util.PsiUtilBase;
import com.intellij.util.Consumer;
import com.intellij.util.messages.MessageBus;
import com.intellij.util.messages.MessageBusConnection;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import run.mone.m78.ip.bo.*;
import run.mone.m78.ip.common.*;
import run.mone.m78.ip.service.CodeService;
import run.mone.m78.ip.service.UltramanService;
import run.mone.m78.ip.util.ProjectUtils;
import run.mone.ultraman.bo.ParamsInfo;
import run.mone.ultraman.event.AthenaEventBus;
import run.mone.ultraman.listener.AthenaFileDocumentManagerListener;
import run.mone.ultraman.listener.AthenaFileEditorManagerListener;
import run.mone.ultraman.listener.AthenaMessageListenerImpl;
import run.mone.ultraman.service.AutoFlushBizService;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author goodjava@qq.com
 */
public class TeslaAppComponent implements ApplicationComponent {

    private static final Logger logger = Logger.getInstance(OpenImageConsumer.class);

    private boolean open = false;

    @Override
    public void initComponent() {
        UltramanService.getInstance().init();

        MessageBus bus = ApplicationManager.getApplication().getMessageBus();
        @NotNull MessageBusConnection connection = bus.connect();
        connection.subscribe(AppTopics.FILE_DOCUMENT_SYNC, new AthenaFileDocumentManagerListener());
        connection.subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, new AthenaFileEditorManagerListener());
        connection.subscribe(Const.ATHENA_TOPIC, new AthenaMessageListenerImpl());
        EditorFactory.getInstance().addEditorFactoryListener(new EditorFactoryListener() {
            @Override
            public void editorCreated(@NotNull EditorFactoryEvent event) {
                event.getEditor().getSelectionModel().addSelectionListener(new SelectionListener() {
                    @Override
                    public void selectionChanged(@NotNull SelectionEvent e) {
                        if (AthenaEventBus.ins().getListener().getParamsInfoConsumer() != null) {
                            Document document = e.getEditor().getDocument();
                            PsiFile psiFile = PsiUtilBase.getPsiFileInEditor(e.getEditor(), e.getEditor().getProject());
                            if (psiFile instanceof PsiJavaFile) {
                                String selectedText = document.getText(e.getNewRange());
                                if (StringUtils.isNotEmpty(selectedText)) {
                                    PsiMethod method = CodeService.getMethod(e.getEditor().getProject());
                                    if (null != method) {
                                        List<String> list = Arrays.stream(method.getParameterList().getParameters()).map(it -> it.getType().getCanonicalText()).collect(Collectors.toList());
                                        ParamsInfo info = new ParamsInfo();
                                        info.setParams(new Gson().toJson(list));
                                        AthenaEventBus.ins().post(info);
                                    }
                                }
                            }
                        }
                    }
                });
            }
        });
        EditorFactory.getInstance().getEventMulticaster().addDocumentListener(new DocumentListener() {
            @Override
            public void documentChanged(@NotNull DocumentEvent event) {
                Document document = event.getDocument();
                VirtualFile file = FileDocumentManager.getInstance().getFile(document);
                AutoFlushBizService.notifyDocumentChanged(file);

                int offset = event.getOffset();
                int newLength = event.getNewLength();
//                System.out.printf("%d %d %d %d%n", event.getOldLength(), event.getNewLength(), event.getMoveOffset(), document.getTextLength());
                // actual logic depends on which line we want to call 'changed' when '\n' is inserted
                int firstLine = document.getLineNumber(offset);
                int lastLine = newLength == 0 ? firstLine : document.getLineNumber(offset + newLength <= document.getTextLength() ? offset + newLength : offset + newLength - 1);
                if (firstLine == lastLine) {
                    return;
                }
                String last = document.getText().substring(document.getLineStartOffset(firstLine), document.getLineEndOffset(firstLine));
//                System.out.printf("new line %d %d %s%n", firstLine, lastLine, last);
                if (!last.contains("//#:")) {
                    return;
                }
                last = last.trim();
                String all = document.getText();
                String[] codeReq = last.split("//#:");
                Map<String, String> paramMap = new HashMap<>();
                paramMap.put("full_code", all);
                paramMap.put("origin_code", last);
                paramMap.put("requirements", codeReq[1]);
                CodeService.generateCodeWithAi5(GenerateCodeReq.builder().build(), ProjectUtils.project(), "call_rika", null, paramMap, new AthenaBiConsumer(), new MessageConsumer() {
                    public void onEvent(AiMessage message) {
                        String x = message.getText();
                        try {
                            ObjectMapper mapper = new ObjectMapper();
                            JsonNode node = mapper.readTree(x);
                            String params = node.at("/arguments").asText();
                            node = mapper.readTree(params);
                            String tf = node.at("/fixedCodes").asText();
                            String to = node.at("/originalCodes").asText();
                            List<String> parsed = parse(to, tf);
                            String fixed = parsed.get(1);
                            String origin = parsed.get(0);
                            if (!to.contains("\n")) {
                                fixed = fixed.replace("\n", " ");
                            }
                            String finalFixed = fixed;
                            ApplicationManager.getApplication().invokeLater(() -> {
                                document.setText(document.getText().replace(origin, finalFixed));
                            });
                        } catch (Exception e) {

                        }
                    }
                });
            }
        });
        if (!open) {
            return;
        }
        String chatServer = ConfigUtils.getConfig().getChatServer();
        //处理接收到的信息
        new Thread(() -> {
            try {
                if (StringUtils.isEmpty(chatServer)) {
                    return;
                }

            } catch (Throwable ex) {
                logger.error(ex.getMessage(), ex);
            }

        }).start();


        new Thread(() -> {
            while (true) {
                Map<String, Object> message = MessageQueue.ins().pollSend();
                if (null != message) {
                    Map<String, Object> m = new HashMap<>(10);
                    m.put("uri", "/mtop/arch/im");
                    m.putAll(message);
                }
            }
        }).start();

    }

    private List<String> parse(String str1, String str2) {
        str1 = str1.trim();
        str2 = str2.trim();
        int start = 0;
        int end = 0;
        for (; start < str1.length() && start < str2.length(); start++) {
            if (str1.charAt(start) != str2.charAt(start)) {
                break;
            }
        }
        for (; end < str1.length() && end < str2.length(); end++) {
            if (str1.charAt(str1.length() - 1 - end) != str2.charAt(str2.length() - 1 - end)) {
                break;
            }
        }
        List<String> res = new ArrayList<>();
        res.add(str1.substring(start, str1.length() - end).trim());
        res.add(str2.substring(start, str2.length() - end).trim());
        return res;
    }

    @NotNull
    private java.util.function.Consumer<String> messageConsumer() {
        return msg -> {

            logger.info("message consumer msg:" + msg);
            final Map<String, Object> m = new Gson().fromJson(msg, Map.class);

            if (null == m || null == m.get("cmd")) {
                logger.warn("msg:" + m);
                return;
            }

            try {
                //收到通知
                if (m.get("cmd").toString().equals("notify")) {
                    String data = m.get("data").toString();
                    logger.info("notify:" + data);

                    ApplicationManager.getApplication().invokeLater(() -> {
                        Messages.showMessageDialog(data, "notify", Messages.getInformationIcon());
                    });
                    return;
                }

                if (m.get("cmd").toString().equals("login")) {
                    Response<List<UserVo>> response = new Gson().fromJson(msg, new TypeToken<Response<List<UserVo>>>() {
                    }.getType());
                    MessageQueue.ins().offer(response);
                    return;
                }

                //有人加入聊天室
                if (m.get("cmd").toString().toUpperCase().equals("login_user_info")) {
                    Response<UserVo> response = new Gson().fromJson(msg, new TypeToken<Response<UserVo>>() {
                    }.getType());
                    MessageQueue.ins().offer(response);
                    return;
                }

                if (m.get("cmd").toString().equals("talk_message")) {
                    talk(msg);
                    return;
                }

                //打开推广
                if (m.get("cmd").toString().equals("ad")) {
                    openAd(m);
                    return;
                }

                //打开文本
                if (m.get("cmd").toString().equals("review")) {
                    openText(m);
                    return;
                }

                if (m.get("cmd").toString().equals("logout_msg")) {
                    Response<String> response = new Gson().fromJson(msg, new TypeToken<Response<String>>() {
                    }.getType());
                    MessageQueue.ins().offer(response);
                    return;
                }
            } catch (Exception ex) {
                logger.error(ex.getMessage(), ex);
            }
        };
    }

    private void talk(String msg) {
        try {
            Response<String> response = new Gson().fromJson(msg, new TypeToken<Response<String>>() {
            }.getType());
            MessageQueue.ins().offer(response);
        } catch (Exception ex) {
            System.out.println("talk ex:" + ex.getMessage());
        }
    }

    /**
     * 打开广告
     *
     * @param m
     */
    private void openAd(Map<String, Object> m) {
        ApplicationManager.getApplication().invokeLater(() -> {

            DataManager.getInstance().getDataContextFromFocusAsync().onSuccess(new java.util.function.Consumer<DataContext>() {
                @Override
                public void accept(DataContext dataContext) {
                    new OpenImageConsumer(m.get("data").toString()).accept(dataContext);
                }
            });

//            DataManager.getInstance().getDataContextFromFocus()
//                    .doWhenDone((Consumer<DataContext>) (dataContext -> new OpenImageConsumer(m.get("data").toString()).accept(dataContext)))
//                    .doWhenRejected((Consumer<String>) logger::error);

        });
    }


    /**
     * 打开文本
     */
    private void openText(Map<String, Object> m) {
        List<String> list = (List<String>) m.get("data");

        ApplicationManager.getApplication().invokeLater(() -> {

            DataManager.getInstance().getDataContextFromFocus()
                    .doWhenDone((Consumer<DataContext>) (dataContext ->
                            new OpenTextConsumer(list.get(0), list.get(1)).accept(dataContext)))
                    .doWhenRejected((Consumer<String>) logger::error);

        });
    }
}
