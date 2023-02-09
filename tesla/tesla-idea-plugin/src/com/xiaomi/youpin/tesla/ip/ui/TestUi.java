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

package com.xiaomi.youpin.tesla.ip.ui;

import com.aliyun.oss.*;
import com.aliyun.oss.model.*;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.*;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TestUi extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextPane textPane1;
    private JButton button1;

    public TestUi() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        buttonCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);


        init();
        button1.addActionListener(e -> {
            JFileChooser jfc = new JFileChooser();
            jfc.setFileSelectionMode(JFileChooser.FILES_ONLY);
            jfc.showDialog(new JLabel(), "选择");
            File file = jfc.getSelectedFile();
            if (file.isDirectory()) {
                System.out.println("文件夹:" + file.getAbsolutePath());
            } else if (file.isFile()) {
                System.out.println("文件:" + file.getAbsolutePath());
            }
            System.out.println(jfc.getSelectedFile().getName());
            URL url = uploadFile(bucketName, jfc.getSelectedFile());
            setClipboardString(url.toString());
        });
    }


    public static void setClipboardString(String text) {
        // 获取系统剪贴板
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        // 封装文本内容
        Transferable trans = new StringSelection(text);
        // 把文本内容设置到系统剪贴板
        clipboard.setContents(trans, null);
    }


    /**
     * 从剪贴板中获取文本（粘贴）
     */
    public static String getClipboardString() {
        // 获取系统剪贴板
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();

        // 获取剪贴板中的内容
        Transferable trans = clipboard.getContents(null);

        if (trans != null) {
            // 判断剪贴板中的内容是否支持文本
            if (trans.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                try {
                    // 获取剪贴板中的文本内容
                    String text = (String) trans.getTransferData(DataFlavor.stringFlavor);
                    return text;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }


    private void init() {
        JTextPane textPane = textPane1;
        textPane.setMaximumSize(new Dimension(100, 100));
        StyledDocument doc = (StyledDocument) textPane.getDocument();

        Style style = doc.addStyle("StyleName", null);
        try {
            StyleConstants.setIcon(style, scaledImage("https://static.runoob.com/images/demo/demo1.jpg"));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        try {
            doc.insertString(doc.getLength(), "<img=https://static.runoob.com/images/demo/demo1.jpg>", style);
        } catch (BadLocationException e) {
            e.printStackTrace();
        }

        System.out.println(textPane1.getText());
    }


    private ImageIcon scaledImage(String location) throws MalformedURLException {
        Image image = Toolkit.getDefaultToolkit().getImage(new URL(location));
        image = image.getScaledInstance(120, 120, Image.SCALE_SMOOTH);
        return new ImageIcon(image);
    }

    private void onOK() {

        System.out.println(textPane1.getText());
//        fileMeta();

//        getUrl(key);
//        uploadFile(bucketName,new File("/Users/zhangzhiyong/Desktop/girl2.jpeg"));
        listFile();
    }


    String endpoint = "oss-cn-beijing.aliyuncs.com";
    String accessKeyId = "";
    String accessKeySecret = "";
    String bucketName = "datazzy";
    String key = "girl.jpeg";

    public URL getUrl(String key) {
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        Date expiration = new Date(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(7));
        URL url = ossClient.generatePresignedUrl(bucketName, key, expiration);
        return url;
    }


    public void listFile() {
        OSSClient ossClient = new OSSClient(endpoint, accessKeyId, accessKeySecret);
        ObjectListing objectListing = ossClient.listObjects(bucketName, "");
        List<OSSObjectSummary> sums = objectListing.getObjectSummaries();
        for (OSSObjectSummary s : sums) {
            System.out.println(s.getKey() + "---->" + getUrl(s.getKey()));
        }
        ossClient.shutdown();
    }


    public URL uploadFile(String bucketName, File file) {
        OSSClient client = new OSSClient(endpoint, accessKeyId, accessKeySecret);
        if (!client.doesBucketExist(bucketName)) {
            client.createBucket(bucketName);
        }
        PutObjectResult por = client.putObject(bucketName, file.getName(), file);
        URL url = getUrl(file.getName());
        client.shutdown();
        System.out.println(url);
        return url;
    }


    private void fileMeta() {
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        try {
            // Gets the object metadata information.
            ObjectMetadata metadata = ossClient.getObjectMetadata(bucketName, key);
            System.out.println(metadata.getContentType());
            System.out.println(metadata.getLastModified());
            System.out.println(metadata.getUserMetadata().get("meta"));


            OSSObject obj = ossClient.getObject(bucketName, key);
            System.out.println(obj);


        } catch (OSSException oe) {
            System.out.println("Caught an OSSException, which means your request made it to OSS, "
                    + "but was rejected with an error response for some reason.");
            System.out.println("Error Message: " + oe.getErrorCode());
            System.out.println("Error Code:       " + oe.getErrorCode());
            System.out.println("Request ID:      " + oe.getRequestId());
            System.out.println("Host ID:           " + oe.getHostId());
        } catch (ClientException ce) {
            System.out.println("Caught an ClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with OSS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message: " + ce.getMessage());
        } finally {
            /*
             * Do not forget to shut down the client finally to release all allocated resources.
             */
            ossClient.shutdown();
        }
    }


    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    public static void main(String[] args) {
        TestUi dialog = new TestUi();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
