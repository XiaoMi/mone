package run.mone.m78.ip.ui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class UltramanUi extends JDialog {

    private JPanel contentPane;
    private JButton buttonOK;

    private JPanel panel1;
    private JLabel imageLabel;
    private JLabel descLabel;


    public UltramanUi() {
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        });

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onOK();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onOK();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);


        ImageIcon img = new ImageIcon("/tmp/a.jpeg");
        imageLabel.setIcon(img);
        imageLabel.setText("");
        this.descLabel.setText("就算化成灰,我的bug也不会放过你;所以,请相信你自己,然后,也请给我发红包");

    }

    private void onOK() {
        // add your code here
        dispose();
    }

    public static void main(String[] args) {
        UltramanUi dialog = new UltramanUi();
        dialog.pack();
        dialog.setVisible(true);
        System.exit(0);
    }

}
