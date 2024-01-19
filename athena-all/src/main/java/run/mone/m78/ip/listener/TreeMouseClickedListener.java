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

import com.intellij.ide.BrowserUtil;
import run.mone.m78.ip.bo.SpiderUrl;
import run.mone.m78.ip.service.ImageService;
import run.mone.m78.ip.service.MusicService;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author goodjava@qq.com
 * @Date 2021/11/10 10:59
 */
public class TreeMouseClickedListener extends MouseAdapter {

    private JTree tree1;

    private JTextField textField1;

    private JPopupMenu popupMenu;

    public TreeMouseClickedListener(JTree tree1, JTextField textField1, JPopupMenu popupMenu) {
        this.tree1 = tree1;
        this.textField1 = textField1;
        this.popupMenu = popupMenu;
    }

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {

        if (mouseEvent.getClickCount() == 2) {
            TreePath path = tree1.getPathForLocation(mouseEvent.getX(), mouseEvent.getY());
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
            String data = node.toString();
            System.out.println(data);


            String text = textField1.getText();

            if (text.equals("image")) {
                new ImageService().openImage(data);
                return;
            }

            if (text.equals("music")) {
                MusicService service = MusicService.ins();
                service.stop();

                Object root = tree1.getModel().getRoot();
                int count = tree1.getModel().getChildCount(root);
                List<String> l = new ArrayList<>();
                for (int i = 0; i < count; i++) {
                    Object v = tree1.getModel().getChild(root, i);
                    l.add(v.toString());
                }

                service.playWithUrl(data);
                return;
            }

            //打开爬虫
            if (text.startsWith("spider")) {
                SpiderUrl url = (SpiderUrl) node.getUserObject();
                BrowserUtil.browse(url.getUrl());
                return;
            }

        }

        //中键
        if (mouseEvent.getButton() == MouseEvent.BUTTON3) {
            if (textField1.getText().equals("task")) {
                int x = mouseEvent.getX();
                int y = mouseEvent.getY();
                popupMenu.show(tree1, x, y);
            }
        }

    }
}
