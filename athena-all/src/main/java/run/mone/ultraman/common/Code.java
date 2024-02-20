package run.mone.ultraman.common;

import com.google.common.base.Strings;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import run.mone.m78.ip.bo.PromptInfo;
import run.mone.m78.ip.service.CodeService;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.stream.IntStream;

/**
 * @author goodjava@qq.com
 * @date 2023/5/26 16:47
 */
@Data
public class Code {

    private Document document;

    private PsiFile psiFile;

    private int offset;

    private Project project;

    private Editor editor;

    private int incrNum = 0;

    private PromptInfo promptInfo;

    /**
     * 需要引入end,不然生成的代码里有数字,就不太好处理了,这个end代表这一行结束了
     */
    private boolean end = true;

    /**
     * 是否需要insert 这行,有的情况下是不需要的
     */
    private boolean needInsert = true;

    public void append(String c) {
        String str = removeEnter(c);
        if (isNum(str) && end) {
            //move
            int lineNum = Integer.parseInt(str.trim());
            lineNum += incrNum;
            offset = document.getLineStartOffset(lineNum - 1);
            int lineEndOffset = document.getLineEndOffset(lineNum - 1);
            int num = calWhitespaceNum(document, offset, lineEndOffset);
            needInsert = needInsert(lineNum);
            if (needInsert) {
                WriteCommandAction.runWriteCommandAction(project, () -> {
                    document.insertString(offset + num, "\n" + Strings.repeat(" ", num));
                    PsiDocumentManager.getInstance(project).commitDocument(document);
                    incrNum++;
                });
                offset += num;
            }
            end = false;
        } else if (isEnterOrSpace(str)) {
            //忽略
        } else if (isEndStr(str)) {
            end = true;
            needInsert = true;
        } else {
            //输入
            if (needInsert) {
                WriteCommandAction.runWriteCommandAction(project, () -> {
                    document.insertString(offset, str);
                    PsiDocumentManager.getInstance(project).commitDocument(document);
                    offset += str.length();
                });
            }
        }


    }

    private boolean needInsert(int lineNum) {
        boolean checkAnno = Boolean.valueOf(this.promptInfo.getLabels().getOrDefault("check_anno", "false"));
        if (checkAnno) {
            String currContent = this.getLineContent(document, lineNum);
            PsiJvmModifiersOwner pmo = null;
            if (CodeService.isClass(currContent)) {
                pmo = CodeService.getPsiClassWithLineNum(project, document, lineNum);
            } else if (CodeService.isPrivateField(currContent)) {
                pmo = CodeService.getPsiFieldWithLineNum(project, document, lineNum);
            } else {
                pmo = CodeService.getPsiMethodWithLineNum(project, document, lineNum);
            }
            PsiJvmModifiersOwner pmot = pmo;
            if (null != pmo) {
                String[] array = this.promptInfo.getLabels().getOrDefault("skip", "").split(",");
                return ApplicationManager.getApplication()
                        .runReadAction((Computable<Boolean>) () -> !Arrays.stream(pmot.getAnnotations())
                                .filter(it -> Arrays.stream(array)
                                        .filter(it2 -> it2.equals(it.getQualifiedName())).findAny().isPresent())
                                .findAny().isPresent()
                        );
            }
        }
        return true;
    }

    private boolean isEndStr(String str) {
        return str.trim().equals("✓");
    }


    private String getLineContent(Document document, int lineNumber) {
        int startOffset = document.getLineStartOffset(lineNumber - 1);
        int endOffset = document.getLineEndOffset(lineNumber - 1);
        String lineContent = document.getText(new TextRange(startOffset, endOffset));
        return lineContent;
    }

    private boolean needInsert(String content) {
        String str = this.promptInfo.getLabels().getOrDefault("skip", "");
        if (StringUtils.isNotEmpty(str)) {
            String[] array = str.split(",");
            return !IntStream.range(0, array.length).filter(i -> content.contains(array[i])).findAny().isPresent();
        }
        return true;
    }


    private int calWhitespaceNum(Document document, int lineStartOffset, int lineEndOffset) {
        int num = 0;
        String lineText = document.getText(new TextRange(lineStartOffset, lineEndOffset));
        for (int i = 0; i < lineText.length(); i++) {
            if (!Character.isWhitespace(lineText.charAt(i))) {
                break;
            }
            num++;
        }
        return num;
    }


    private boolean isNum(String str) {
        str = str.trim();
        try {
            Integer.parseInt(str);
            return true;
        } catch (Throwable ex) {
            return false;
        }
    }

    private boolean isEnterOrSpace(String str) {
        return str.trim().equals("\n") || "".equals(str.trim());
    }

    private String removeEnter(String str) {
        return str.replaceAll("\n", "");
    }

}
