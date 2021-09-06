package io.github.eb4j.ebview.gui.dialogs;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class AboutDialog extends JDialog {
    private JLabel versionLabel = new JLabel();
    private JPanel buttonPanel = new JPanel();
    private JButton buttonOK = new JButton("OK");
    JTextArea aboutTextArea = new JTextArea();

    public AboutDialog(Frame parent) {
        setModal(true);
        //
        buttonPanel.add(buttonOK);
        aboutTextArea.setEditable(false);
        aboutTextArea.setText("About EBViewer");
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
