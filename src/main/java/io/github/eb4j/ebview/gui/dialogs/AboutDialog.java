package io.github.eb4j.ebview.gui.dialogs;

import io.github.eb4j.ebview.utils.LStrings;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class AboutDialog extends JDialog {
    private final JLabel versionLabel = new JLabel();
    private final JPanel buttonPanel = new JPanel();
    private final JButton buttonOK = new JButton("OK");
    JTextArea aboutTextArea = new JTextArea();

    public AboutDialog(Frame parent) {
        setModal(true);
        //
        versionLabel.setText("EBView Version 1.0.0");
        //
        buttonPanel.add(buttonOK);
        aboutTextArea.setPreferredSize(new Dimension(200, 100));
        aboutTextArea.setEditable(false);
        aboutTextArea.setText(LStrings.getString("ABOUT_APP"));
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(versionLabel, BorderLayout.NORTH);
        getContentPane().add(aboutTextArea, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        buttonOK.addActionListener(e -> onOK());
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        buttonPanel.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        invalidate();
        pack();
        setLocationRelativeTo(parent);
    }

    private void onCancel() {
        dispose();
    }

    private void onOK() {
        dispose();
    }

}
