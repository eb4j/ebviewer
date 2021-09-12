package io.github.eb4j.ebview.gui.dialogs;

import io.github.eb4j.ebview.utils.LStrings;
import io.github.eb4j.ebview.utils.ResourceUtil;
import io.github.eb4j.ebview.utils.VersionString;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.ScrollPaneConstants;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class AboutDialog extends JDialog {
    private final JLabel versionLabel = new JLabel();
    private final JPanel buttonPanel = new JPanel();
    private final JButton buttonOK = new JButton("OK");
    private final JLabel iconLabel = new JLabel();
    private final JTextPane aboutTextArea = new JTextPane();
    private final JTextPane copyrightTextArea = new JTextPane();

    public AboutDialog(final Frame parent) {
        setModal(true);
        setTitle(LStrings.getString("ABOUT_TITLE"));
        //
        versionLabel.setText(String.format(LStrings.getString("ABOUT_VERSION_LABEL"), VersionString.VERSION));
        //
        buttonPanel.add(buttonOK);
        aboutTextArea.setPreferredSize(new Dimension(300, 100));
        aboutTextArea.setEditable(false);
        aboutTextArea.setText(LStrings.getString("ABOUT_APP"));
        copyrightTextArea.setEditable(false);
        iconLabel.setIcon(new ImageIcon(ResourceUtil.APP_ICON_32X32));
        JPanel panel1 = new JPanel();
        panel1.setLayout(new BoxLayout(panel1, BoxLayout.PAGE_AXIS));
        panel1.add(iconLabel);
        panel1.add(aboutTextArea);
        JScrollPane copyrightPane = new JScrollPane(copyrightTextArea);
        copyrightPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        copyrightTextArea.setText(LStrings.getString("ABOUT_COPYRIGHT"));
        copyrightPane.getVerticalScrollBar().setValue(0);
        copyrightPane.setPreferredSize(new Dimension(420, 200));
        panel1.add(copyrightPane);
        //
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(versionLabel, BorderLayout.NORTH);
        getContentPane().add(panel1, BorderLayout.CENTER);
        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        buttonOK.addActionListener(e -> onOK());
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(final WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        buttonPanel.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

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
