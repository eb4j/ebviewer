package io.github.eb4j.ebview;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.TitledBorder;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.List;

/**
 * Swing main window.
 * @author Hiroshi Miura
 */
public class MainWindow extends JFrame {
    private JPanel panel1;
    private JTextField searchWordField;
    private JButton searchButton;
    private JScrollPane articlePane;
    private TitledBorder border;
    private JScrollPane historyPane;
    private DefaultListModel<String> historyModel = new DefaultListModel<>();

    public MainWindow(final EBDict ebDict) {
        super("EBViewer");
        setPreferredSize(new Dimension(515, 405));
        setLayout(new BorderLayout());
        //
        panel1 = new JPanel();
        panel1.setLayout(new FlowLayout());
        searchWordField = new JTextField();
        searchWordField.setPreferredSize(new Dimension(300, 30));
        searchButton = new JButton();
        searchButton.setText("Search");
        panel1.add(searchWordField);
        panel1.add(searchButton);
        //
        DictionaryPane dictionaryPane = new DictionaryPane();
        articlePane = new JScrollPane(dictionaryPane);
        articlePane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        //
        border = new TitledBorder("History");
        border.setTitleJustification(TitledBorder.CENTER);
        border.setTitlePosition(TitledBorder.TOP);

        JList<String> history = new JList<>(historyModel);
        historyPane = new JScrollPane(history);
        historyPane.setPreferredSize(new Dimension(100, -1));
        historyPane.setBorder(border);
        //
        getContentPane().add(panel1, BorderLayout.NORTH);
        getContentPane().add(articlePane, BorderLayout.CENTER);
        getContentPane().add(historyPane, BorderLayout.EAST);
        //
        searchWordField.addActionListener(e -> {
            String word = searchWordField.getText();
            historyModel.add(0, word);
            new Thread(() -> {
                List<DictionaryEntry> result = ebDict.readArticles(word);
                SwingUtilities.invokeLater(() -> {
                    dictionaryPane.setFoundResult(result);
                    dictionaryPane.setCaretPosition(0);
                });
            }).start();
        });
        searchButton.addActionListener(e -> {
            String word = searchWordField.getText();
            historyModel.add(0, word);
            new Thread(() -> {
                List<DictionaryEntry> result = ebDict.readArticles(word);
                SwingUtilities.invokeLater(() -> {
                    dictionaryPane.setFoundResult(result);
                    dictionaryPane.setCaretPosition(0);
                });
            }).start();
        });
        history.addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) {
                // The user is still manipulating the selection.
                return;
            }
            Object obj = history.getSelectedValue();
            if (obj != null) {
                searchWordField.setText(obj.toString());
            }
        });
        //
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setResizable(true);
        setVisible(true);
    }

}
