package io.github.eb4j.ebview.gui;

import io.github.eb4j.ebview.data.DictionaryEntry;
import io.github.eb4j.ebview.dictionary.DictionariesManager;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
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
public final class MainWindow extends JFrame {
    JTextField searchWordField;
    DefaultListModel<String> headingsModel;
    DictionaryPane dictionaryPane;

    final DictionariesManager dictionariesManager;
    final DefaultListModel<String> historyModel = new DefaultListModel<>();
    final JButton searchButton = new JButton();

    private JList<String> headingsList;
    private JList<String> history;

    public MainWindow(final DictionariesManager dictionariesManager) {
        super("EBViewer");
        this.dictionariesManager = dictionariesManager;
        initializeGUI();
        setActions();
        pack();
        setResizable(true);
        setVisible(true);
    }

    public void setResult(final List<DictionaryEntry> result) {
       headingsModel.addAll(result.stream().map(DictionaryEntry::getWord).collect(Collectors.toList()));
       dictionaryPane.setFoundResult(result);
       dictionaryPane.setCaretPosition(0);
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
        searchButton.setText("Search");
        panel1.add(searchWordField);
        panel1.add(searchButton);
        //
        headingsModel = new DefaultListModel<>();
        headingsList = new JList<>(headingsModel);
        JScrollPane headingsPane = new JScrollPane(headingsList);
        headingsPane.setPreferredSize(new Dimension(100, -1));
        //
        dictionaryPane = new DictionaryPane();
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
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void setActions() {
        searchWordField.addActionListener(e -> {
            Searcher searchSwingWorker = new Searcher(this);
            searchSwingWorker.execute();
        });

        searchButton.addActionListener(e -> {
            Searcher searchSwingWorker = new Searcher(this);
            searchSwingWorker.execute();
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
    }
}
