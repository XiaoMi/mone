package com.xiaomi.youpin.tesla.ip.service;

import lombok.SneakyThrows;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;

/**
 * @author goodjava@qq.com
 * @date 2023/5/21 23:27
 */
public class ClipboardService {


    /**
     * 从剪贴板复制东西
     * @return
     */
    @SneakyThrows
    public static String getData() {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Clipboard clipboard = toolkit.getSystemClipboard();
        DataFlavor[] flavors = new DataFlavor[]{
                DataFlavor.stringFlavor // 获取剪贴板中的文本内容
        };
        Transferable contents = clipboard.getContents(null);
        String text = (String) contents.getTransferData(flavors[0]);
        return text;
    }


}
