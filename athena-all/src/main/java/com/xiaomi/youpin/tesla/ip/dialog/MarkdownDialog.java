//package com.xiaomi.youpin.tesla.ip.dialog;
//
//import com.google.common.collect.Lists;
//import com.intellij.openapi.application.ApplicationManager;
//import com.intellij.openapi.editor.Document;
//import com.intellij.openapi.editor.Editor;
//import com.intellij.openapi.editor.EditorFactory;
//import com.intellij.openapi.editor.event.DocumentEvent;
//import com.intellij.openapi.editor.event.DocumentListener;
//import com.intellij.openapi.fileTypes.FileType;
//import com.intellij.openapi.fileTypes.FileTypeManager;
//import com.intellij.openapi.project.Project;
//import com.intellij.openapi.ui.DialogWrapper;
//import com.vladsch.flexmark.ext.gfm.strikethrough.StrikethroughExtension;
//import com.vladsch.flexmark.ext.tables.TablesExtension;
//import com.vladsch.flexmark.ext.toc.TocExtension;
//import com.vladsch.flexmark.ext.wikilink.WikiLinkExtension;
//import com.vladsch.flexmark.html.HtmlRenderer;
//import com.vladsch.flexmark.parser.Parser;
//import com.vladsch.flexmark.util.ast.Node;
//import com.vladsch.flexmark.util.data.MutableDataSet;
//import lombok.Getter;
//
//import javax.swing.*;
//import java.awt.*;
//import java.awt.datatransfer.Clipboard;
//import java.awt.datatransfer.StringSelection;
//import java.io.BufferedWriter;
//import java.io.File;
//import java.io.FileWriter;
//import java.io.IOException;
//import java.util.Arrays;
//
///**
// * @author goodjava@qq.com
// * @author caobaoyu
// * @date 2023/7/13
// * 一个md编辑器的dialog
// */
//public class MarkdownDialog extends DialogWrapper {
//    private Project project;
//    private JButton saveButton;
//    private JButton copyButton;
//    private Editor editor;
//    private JFrame frame;
//    @Getter
//    private DialogResult dialogResult = new DialogResult();
//
//
//    public MarkdownDialog(Project project) {
//        super(true);
//        this.project = project;
//        init();
//        setTitle("Athena Markdown Editor");
//        editor = createEditor(project);
//        createButtons();
//    }
//
//    @Override
//    public void show() {
//        if (editor != null) {
//            JPanel editorPanel = new JPanel(new BorderLayout());
//            editorPanel.add(editor.getComponent(), BorderLayout.CENTER);
//            editorPanel.setPreferredSize(new Dimension(800, 600));
//            // 添加Markdown预览
//            JEditorPane previewPane = new JEditorPane();
//            previewPane.setEditable(false);
//            previewPane.setContentType("text/html");
//            JScrollPane previewScrollPane = new JScrollPane(previewPane);
//            previewScrollPane.setPreferredSize(new Dimension(400, 600));
//            JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, editorPanel, previewScrollPane);
//            splitPane.setResizeWeight(0.5);
//            splitPane.setOneTouchExpandable(true);
//            splitPane.setDividerSize(2); // 设置分隔条的宽度
//
//            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
//            buttonPanel.add(copyButton);
//            buttonPanel.add(saveButton);
//
//            frame = new JFrame("Markdown Editor");
//            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//            frame.getContentPane().add(splitPane, BorderLayout.CENTER);
//            frame.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
//            frame.pack();
//            frame.setVisible(true);
//            // 更新Markdown预览
//            editor.getDocument().addDocumentListener(new DocumentListener() {
//                @Override
//                public void documentChanged(DocumentEvent event) {
//                    ApplicationManager.getApplication().runReadAction(() -> {
//                        String markdown = event.getDocument().getText();
//                        MutableDataSet options = new MutableDataSet();
//                        options.set(Parser.EXTENSIONS, Arrays.asList(
//                                TablesExtension.create(),
//                                TocExtension.create(),
//                                StrikethroughExtension.create(),
//                                WikiLinkExtension.create()
//                        ));
//                        Parser parser = Parser.builder(options).build();
//                        Node document = parser.parse(markdown);
//                        HtmlRenderer renderer = HtmlRenderer.builder(options).build();
//                        String html = renderer.render(document);
//                        previewPane.setText(html);
//                    });
//                }
//
//
//
//
//            });
//        }
//    }
//
//    @Override
//    protected JComponent createCenterPanel() {
//        return new JPanel(new FlowLayout(FlowLayout.RIGHT));
//    }
//
//    private Editor createEditor(Project project) {
//        EditorFactory editorFactory = EditorFactory.getInstance();
//        Document document = EditorFactory.getInstance().createDocument("# Hello world");
//        final FileType fileType = FileTypeManager.getInstance().getFileTypeByExtension("md");
//        Editor editor = editorFactory.createEditor(document, project, fileType, false);
//        editor.getSettings().setLineNumbersShown(true);
//        editor.getSettings().setFoldingOutlineShown(true);
//        editor.getSettings().setAutoCodeFoldingEnabled(true);
//        editor.getSettings().setAdditionalLinesCount(1);
//        editor.getSettings().setAdditionalColumnsCount(1);
//        editor.getSettings().setRightMarginShown(true);
//        editor.getSettings().setRightMargin(80);
//        editor.getSettings().setCaretRowShown(true);
//        editor.getSettings().setUseSoftWraps(true);
//        editor.getSettings().setSoftMargins(Lists.newArrayList(80));
//        editor.getSettings().setTabSize(4);
//        editor.getSettings().setUseTabCharacter(false);
//
//        return editor;
//    }
//
//
//    private void createButtons() {
//        saveButton = new JButton("Save");
//        copyButton = new JButton("Copy");
//
//        saveButton.addActionListener(e -> onSaveButtonClick());
//        copyButton.addActionListener(e -> onCopyButtonClick());
//    }
//
//    private void onSaveButtonClick() {
//        JPanel panel = new JPanel();
//        String fileName = JOptionPane.showInputDialog(panel, "Enter file name:");
//        if (fileName != null && !fileName.trim().isEmpty()) {
//            try {
//                String path = project.getBaseDir().getPath() + "/" + fileName + ".md";
//                File file = new File(path);
//                BufferedWriter writer = new BufferedWriter(new FileWriter(file));
//                String markdownContent = getMarkdownContent();
//                writer.write(markdownContent);
//                writer.close();
//                putInfo(markdownContent);
//                close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    private void onCopyButtonClick() {
//        String markdownContent = getMarkdownContent();
//        if (!markdownContent.isEmpty()) {
//            StringSelection selection = new StringSelection(markdownContent);
//            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
//            clipboard.setContents(selection, null);
//            putInfo(markdownContent);
//            close();
//        }
//
//    }
//
//    private String getMarkdownContent() {
//        Editor editor = this.editor;
//        if (editor != null) {
//            Document document = editor.getDocument();
//            return document.getText();
//        }
//        return "";
//    }
//
//    public void close() {
//        SwingUtilities.invokeLater(() -> {
//            frame.dispose();
//            this.dispose();
//        });
//    }
//
//    public void putInfo(String msg) {
//        this.dialogResult.getData().put("text", msg);
//    }
//
//}
//
//
//
