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

package run.mone.m78.ip.util;

import com.google.gson.Gson;
import com.intellij.codeInsight.hint.HintManager;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import run.mone.m78.ip.bo.Result;
import run.mone.m78.ip.common.ChromeUtils;

/**
 * @Author goodjava@qq.com
 * @Date 2021/11/7 13:11
 */
public class HintUtils {

    public static void show(final Editor editor, Result result) {
        Gson gson = new Gson();
        String notify = gson.toJson(result);
        ApplicationManager.getApplication().invokeLater(() -> {
            HintManager.getInstance().showInformationHint(editor, notify);
            ChromeUtils.call(editor.getProject().getName(), "showErrorCode", notify, false);
        });
    }

    public static void show(final Editor editor, String message) {
        show(editor, message, false);
    }

    public static void show(final Editor editor, String message, boolean notifyChrome) {
        ApplicationManager.getApplication().invokeLater(() -> {
            if (null != editor) {
                HintManager.getInstance().showInformationHint(editor, message);
                if (notifyChrome) {
                    ChromeUtils.call(editor.getProject().getName(), "showErrorCode", message, true);
                }
            }
        });
    }



    public static void show(String projectName, String message, boolean notifyChrome) {
        ApplicationManager.getApplication().invokeLater(() -> {
            if (notifyChrome) {
                ChromeUtils.call(projectName, "showErrorCode", message, true);
            }
        });
    }

}
