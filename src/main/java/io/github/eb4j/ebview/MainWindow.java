package io.github.eb4j.ebview;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Swing main window.
 * @author Hiroshi Miura
 */
public class MainWindow extends JFrame {
    private JTextField searchWordField;
    private DefaultListModel headingsModel;
    private JList<String> headingsList;
    private final DefaultListModel<String> historyModel = new DefaultListModel<>();
    private JList<String> history;
    //
    private final EBViewer ebViewer;

    public MainWindow(final EBViewer ebViewer) {
        super("EBViewer");
        this.ebViewer = ebViewer;
        initializeGUI();
    }

    private void initializeGUI() {
        setPreferredSize(new Dimension(800, 500));
        setLayout(new BorderLayout());
        //
        // GUI parts
        JPanel panel1 = new JPanel();
        panel1.setLayout(new FlowLayout());
        searchWordField = new JTextField();
        searchWordField.setPreferredSize(new Dimension(500, 30));
        JButton searchButton = new JButton();
        searchButton.setText("Search");
        panel1.add(searchWordField);
        panel1.add(searchButton);
        //
        headingsModel = new DefaultListModel<String>();
        headingsList = new JList<String>(headingsModel);
        JScrollPane headingsPane = new JScrollPane(headingsList);
        headingsPane.setPreferredSize(new Dimension(100, -1));
        //
        DictionaryPane dictionaryPane = new DictionaryPane();
        JScrollPane articlePane = new JScrollPane(dictionaryPane);
        articlePane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        //
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.PAGE_AXIS));
        JTextPane dictionaryInfoPane = new JTextPane();
        dictionaryInfoPane.setPreferredSize(new Dimension(100, 200));
        dictionaryInfoPane.setText("Dictionary info");
        //
        TitledBorder historyTitleBorder = new TitledBorder("History");
        historyTitleBorder.setTitleJustification(TitledBorder.CENTER);
        historyTitleBorder.setTitlePosition(TitledBorder.TOP);
        history = new JList<>(historyModel);
        JScrollPane historyPane = new JScrollPane(history);
        historyPane.setPreferredSize(new Dimension(100, 300));
        historyPane.setBorder(historyTitleBorder);
        infoPanel.add(dictionaryInfoPane);
        infoPanel.add(historyPane);
        //
        getContentPane().add(panel1, BorderLayout.NORTH);
        getContentPane().add(headingsPane, BorderLayout.WEST);
        getContentPane().add(articlePane, BorderLayout.CENTER);
        getContentPane().add(infoPanel, BorderLayout.EAST);
        //
        searchWordField.addActionListener(e -> {
            String word = searchWordField.getText();
            historyModel.add(0, word);
            headingsModel.removeAllElements();
            for (EBDict ebDict: ebViewer.getDictionaries()) {
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
            for (EBDict ebDict: ebViewer.getDictionaries()) {
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
