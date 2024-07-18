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

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class NaviUi extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JButton button1;
    private JButton button2;
    private JButton button3;
    private JButton button4;
    private JButton button5;
    private JButton button6;
    private JButton button7;
    private JButton button8;
    private JButton button9;
    private JButton button10;
    private JButton button11;
    private JButton button12;
    private JButton button13;
    private JButton button14;


    class DataButton {
        public JButton button;
        public String url;

        public DataButton(JButton button) {
            this.button = button;
        }
    }


    private List<DataButton> buttons = new ArrayList<>();

    public NaviUi() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);


        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
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


        this.buttons.add(new DataButton(button1));
        this.buttons.add(new DataButton(button2));
        this.buttons.add(new DataButton(button3));
        this.buttons.add(new DataButton(button4));
        this.buttons.add(new DataButton(button5));
        this.buttons.add(new DataButton(button6));
        this.buttons.add(new DataButton(button7));
        this.buttons.add(new DataButton(button8));
        this.buttons.add(new DataButton(button9));
        this.buttons.add(new DataButton(button10));
        this.buttons.add(new DataButton(button11));
        this.buttons.add(new DataButton(button12));
        this.buttons.add(new DataButton(button13));
        this.buttons.add(new DataButton(button14));

        init();

        this.setSize(this.getWidth()+1,this.getHeight()+1);

    }

    private void go(String url) {
        boolean isSupported = Desktop.isDesktopSupported()
                && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE);
        System.out.println(isSupported);
        if (isSupported) {
            try {
                Desktop.getDesktop().browse(new java.net.URI(url));
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (URISyntaxException ex) {
                ex.printStackTrace();
            }
        }
    }


    private void init() {
        String content = "";
        List<Map> array = new Gson().fromJson(content, new TypeToken<List<Map>>() {
        }.getType());


        IntStream.range(0, 12).parallel().forEach(it -> {
            buttons.get(it).button.setText(array.get(it).get("name").toString());
            SwingUtilities.invokeLater(() -> {
                try {
                    String imgSrc = array.get(it).get("img").toString();
//                    ImageIcon icon = new ImageIcon(new URL(imgSrc), "abc");
//                    icon.setImage(icon.getImage().getScaledInstance(40, 40, Image.SCALE_DEFAULT));
//                    buttons.get(it).button.setIcon(icon);
                    buttons.get(it).button.addActionListener((e) -> {
                        Map m = (Map) ((List) array.get(it).get("urls")).get(0);
                        go(m.get("url").toString());
                    });
                    buttons.get(it).button.setSize(1, 40);
//                    buttons.get(it).button.setVerticalTextPosition(SwingConstants.BOTTOM);
//                    buttons.get(it).button.setHorizontalTextPosition(SwingConstants.CENTER);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });


        });
    }


    private void onCancel() {
        dispose();
    }

    public static void main(String[] args) {
        NaviUi dialog = new NaviUi();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }
}
