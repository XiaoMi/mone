package com.xiaomi.youpin.tesla.ip.listener;

import cn.hutool.core.codec.Base64;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.xiaomi.youpin.infra.rpc.Result;
import com.xiaomi.youpin.tesla.ip.bo.GenerateCodeReq;
import com.xiaomi.youpin.tesla.ip.bo.PromptInfo;
import com.xiaomi.youpin.tesla.ip.bo.chatgpt.*;
import com.xiaomi.youpin.tesla.ip.common.ChromeUtils;
import com.xiaomi.youpin.tesla.ip.common.Prompt;
import com.xiaomi.youpin.tesla.ip.common.PromptType;
import com.xiaomi.youpin.tesla.ip.service.LocalAiService;
import com.xiaomi.youpin.tesla.ip.service.M78Service;
import com.xiaomi.youpin.tesla.ip.service.PromptService;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;
import run.mone.ultraman.background.AthenaTask;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author goodjava@qq.com
 * @date 2023/12/10 15:45
 */
public class VisionHandler {

    public static String handler(Project project, Req req) {
        //传递录制的声音
        if (req.getCmd().equals("sound")) {
//            return saveSound(project, req);
            return saveSound2(project, req);
        }

        //播放声音
        if (req.getCmd().equals("play sound")) {
//            return playSound2(project, req);
            return playSound3(project, req);
        }

        //多模态,可以对图像提问
        if (req.getCmd().equals("vision")) {
//            return vision(project, req);
            return vision2(project, req);
        }
        return "";
    }

    //保存声音(从chatgpt 哪里获取音频文件)
    @NotNull
    private static String saveSound(Project project, Req req) {
        String res;
        String data = req.getData().get("sound");
        String mp3Path = "/tmp/openai.mp3";

        AthenaTask.start(new Task.Backgroundable(project, "save sound", true) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                try {
                    byte[] newData = Base64.decode(data.getBytes());
                    Files.write(Paths.get(mp3Path), newData);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                Pair<Integer, String> r = LocalAiService.callChatgptTranscriptions(new File(mp3Path));
                ChromeUtils.call(project.getName(), r.getValue(), 0);
                if (r.getKey() == 0) {
                    callBot(project, r.getValue());
                }
            }
        });
        res = "ok:" + data.length();
        return res;
    }

    private static String saveSound2(Project project, Req req) {
        String soundInBase64 = req.getData().get("sound");
        String res = "";
        AthenaTask.start(new Task.Backgroundable(project, "speech to text", true) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("text", soundInBase64);
                jsonObject.addProperty("format", "mp3");
                Result<String> transRes = M78Service.speechToText2(jsonObject);
                if (transRes.getCode() == 0) {
                    ChromeUtils.call(project.getName(), transRes.getData(), 0);
                }
            }
        });
        res = "ok:" + soundInBase64.length();
        return res;
    }

    @NotNull
    private static String playSound2(Project project, Req req) {
        String res;
        AthenaTask.start(new Task.Backgroundable(project, "play sound", true) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                Pair<Integer, String> v = playSound(req);
                if (v.getKey() == 0) {
                    String s = v.getValue();
                    ChromeUtils.call(project.getName(), s, 777);
                }
            }
        });
        res = "";
        return res;
    }

    private static String playSound3(Project project, Req req) {
        String res;
        AthenaTask.start(new Task.Backgroundable(project, "text to speech", true) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("text", req.getData().get("input"));
                Result<byte[]> result = M78Service.textToSpeech(jsonObject);
                if (result.getCode() == 0) {
                    String s = new String(result.getData());
                    ChromeUtils.call(project.getName(), s, 777);
                }
            }
        });
        res = "";
        return res;
    }

    //多模态能力(可以识别图片)
    @NotNull
    private static String vision(Project project, Req req) {
        String res;
        String text = req.getData().get("text");
        String image = req.getData().get("image_url");
        ReqMessage r = ReqMessage.builder()
                .content(com.google.common.collect.Lists.newArrayList(
                        ReqContent.builder().type("text").text(text).build(),
                        ReqContent.builder().type("image_url").image_url(ImageUrl.builder().url(image).build()).build()
                )).build();

        AthenaTask.start(new Task.Backgroundable(project, "vision", true) {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                String rv = LocalAiService.vision(VisionReq.builder().messages(com.google.common.collect.Lists.newArrayList(r)).build());
                ChromeUtils.call(project.getName(), rv, 0);
            }
        });
        res = "";
        return res;
    }

    private static String vision2(Project project, Req req) {
        // 将req构建为m78格式的多模态ws请求
        JsonObject jsonReq = new JsonObject();
        jsonReq.addProperty("multimodal", 2);
        jsonReq.addProperty("botId", 100307);
        jsonReq.addProperty("topicId", 4090);
        jsonReq.addProperty("input", req.getData().get("input"));
        jsonReq.addProperty("mediaType", req.getData().get("mediaType"));
        jsonReq.addProperty("postscript", req.getData().get("postscript"));
        M78Service.imageHandle(project, jsonReq);
        return "processing pic...";
    }

    private static Pair<Integer, String> playSound(Req req) {
        return LocalAiService.speech(SpeechReq.builder().input(req.getData().get("input")).build());
    }

    public static void callBot(Project project, String prompt) {
        ApplicationManager.getApplication().invokeLater(() -> {
            PromptInfo promptInfo = Prompt.getPromptInfo("bot");
            PromptType promptType = Prompt.getPromptType(promptInfo);
            PromptService.dynamicInvoke(GenerateCodeReq.builder()
                    .project(project)
                    .promptType(promptType)
                    .promptInfo(promptInfo)
                    .promptName(promptInfo.getPromptName())
                    .param(ImmutableMap.of("code", prompt))
                    .promptName("bot").build());
        });
    }

}
