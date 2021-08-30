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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Swing main window.
 * @author Hiroshi Miura
 */
public class MainWindow extends JFrame {
    private JPanel panel1;
    private JTextField searchWordField;
    private JButton searchButton;
    private JScrollPane headingsPane;
    private DefaultListModel headingsModel;
    private JList<String> headingsList;
    private JScrollPane articlePane;
    private TitledBorder border;
    private JScrollPane historyPane;
    private DefaultListModel<String> historyModel = new DefaultListModel<>();
    private List<EBDict> dictionaries = new ArrayList<>();
    private JList<String> history;

    public MainWindow(final EBDict ebDict) {
        super("EBViewer");
        dictionaries.add(ebDict);
        initializeGUI();
    }

    private void initializeGUI() {
        setPreferredSize(new Dimension(800, 500));
        setLayout(new BorderLayout());
        //
        panel1 = new JPanel();
        panel1.setLayout(new FlowLayout());
        searchWordField = new JTextField();
        searchWordField.setPreferredSize(new Dimension(500, 30));
        searchButton = new JButton();
        searchButton.setText("Search");
        panel1.add(searchWordField);
        panel1.add(searchButton);
        //
        headingsModel = new DefaultListModel<String>();
        headingsList = new JList<String>(headingsModel);
        headingsPane = new JScrollPane(headingsList);
        headingsPane.setPreferredSize(new Dimension(100, -1));
        //
        DictionaryPane dictionaryPane = new DictionaryPane();
        articlePane = new JScrollPane(dictionaryPane);
        articlePane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        //
        border = new TitledBorder("History");
        border.setTitleJustification(TitledBorder.CENTER);
        border.setTitlePosition(TitledBorder.TOP);

        history = new JList<>(historyModel);
        historyPane = new JScrollPane(history);
        historyPane.setPreferredSize(new Dimension(100, -1));
        historyPane.setBorder(border);
        //
        getContentPane().add(panel1, BorderLayout.NORTH);
        getContentPane().add(headingsPane, BorderLayout.WEST);
        getContentPane().add(articlePane, BorderLayout.CENTER);
        getContentPane().add(historyPane, BorderLayout.EAST);
        //
        searchWordField.addActionListener(e -> {
            String word = searchWordField.getText();
            historyModel.add(0, word);
            headingsModel.removeAllElements();
            for (EBDict ebDict: dictionaries) {
                new Thread(() -> {
                    List<DictionaryEntry> result = ebDict.readArticles(word);
                    SwingUtilities.invokeLater(() -> {
                        headingsModel.addAll(result.stream().map(DictionaryEntry::getWord).collect(Collectors.toList()));
                        dictionaryPane.setFoundResult(result);
                        dictionaryPane.setCaretPosition(0);
                    });
                }).start();
            }
        });
        searchButton.addActionListener(e -> {
            String word = searchWordField.getText();
            historyModel.add(0, word);
            headingsModel.removeAllElements();
            for (EBDict ebDict: dictionaries) {
                new Thread(() -> {
                    List<DictionaryEntry> result = ebDict.readArticles(word);
                    SwingUtilities.invokeLater(() -> {
                        headingsModel.addAll(result.stream().map(DictionaryEntry::getWord).collect(Collectors.toList()));
                        dictionaryPane.setFoundResult(result);
                        dictionaryPane.setCaretPosition(0);
                    });
                }).start();
            }
        });
        headingsList.addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) {
                return;
            }
            Object obj = headingsList.getSelectedValue();
            if (obj != null) {
                dictionaryPane.moveToWord(obj.toString());
            }
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
