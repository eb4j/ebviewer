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
import javax.swing.ScrollPaneConstants;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static io.github.eb4j.ebview.utils.ResourceUtil.APP_ICON_16X16;
import static io.github.eb4j.ebview.utils.ResourceUtil.APP_ICON_32X32;

/**
 * Swing main window.
 * @author Hiroshi Miura
 */
public final class MainWindow extends JFrame implements IMainWindow {
    private final Set<String> selectedDicts = new HashSet<>();
    private final List<DictionaryEntry> ourResult = new ArrayList<>();
    private final DefaultListModel<String> historyModel = new DefaultListModel<>();
    private final JButton searchButton = new JButton();
    private final DefaultListModel<String> dictionaryInfoModel = new DefaultListModel<>();

    private final DictionariesManager dictionariesManager;

    private JTextField searchWordField;
    private DefaultListModel<String> headingsModel;
    private DictionaryPane dictionaryPane;
    private MainWindowMenu mainWindowMenu;

    private JList headingsList;
    private JList history;
    private JList dictionaryInfoList;

    public MainWindow(final DictionariesManager dictionariesManager) {
        super("EBViewer");
        this.dictionariesManager = dictionariesManager;
        // Set X11 application class name to make some desktop user interfaces
        // (like Gnome Shell) recognizeT
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Class<?> cls = toolkit.getClass();
        try {
            if (cls.getName().equals("sun.awt.X11.XToolkit")) {
                Field field = cls.getDeclaredField("awtAppClassName");
                field.setAccessible(true);
                field.set(toolkit, "EBViewer");
            }
        } catch (Exception e) {
            // do nothing
        }
        setWindowIcon(this);

        initializeGUI();
        initializeMenu();
        setActions();
        pack();
        setResizable(true);
        setVisible(true);
    }

    public static void setWindowIcon(Window window) {
        List<Image> icons;
        icons = Arrays.asList(APP_ICON_16X16, APP_ICON_32X32);
        window.setIconImages(icons);
    }

    public void setDictionaryList(final List<String> dictList) {
        dictionaryInfoModel.removeAllElements();
        dictionaryInfoModel.addAll(dictList);
        selectedDicts.addAll(dictList);
    }

    public void addToHistory(final String word) {
        historyModel.add(0, word);
    }

    public String getSearchWord() {
        return searchWordField.getText();
    }

    public void updateResult(final List<DictionaryEntry> result) {
        ourResult.clear();
        ourResult.addAll(result);
        updateResult();
    }

    @Override
    public DictionariesManager getDictionariesManager() {
        return dictionariesManager;
    }

    @Override
    public JFrame getApplicationFrame() {
        return this;
    }

    private void updateResult() {
        List<String> wordList = new ArrayList<>();
        List<DictionaryEntry> entries = new ArrayList<>();
        for (DictionaryEntry dictionaryEntry : ourResult) {
            String name = dictionaryEntry.getDictName();
            if (selectedDicts.contains(name)) {
                entries.add(dictionaryEntry);
                wordList.add(String.format("<html><span style='font-style: italic'>%s</span>&nbsp;&nbsp;%s</html>",
                        name.substring(0, 2), dictionaryEntry.getWord()));
            }
        }
        headingsModel.removeAllElements();
        headingsModel.addAll(wordList);
        dictionaryPane.setFoundResult(entries);
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
        headingsList = new JList(headingsModel);
        JScrollPane headingsPane = new JScrollPane(headingsList);
        headingsPane.setPreferredSize(new Dimension(140, -1));
        //
        dictionaryPane = new DictionaryPane();
        JScrollPane articlePane = new JScrollPane(dictionaryPane);
        articlePane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        //
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.PAGE_AXIS));
        //
        dictionaryInfoList = new JList(dictionaryInfoModel);
        JScrollPane dictionaryInfoPane = new JScrollPane(dictionaryInfoList);
        dictionaryInfoPane.setPreferredSize(new Dimension(180, 80));
        //
        TitledBorder historyTitleBorder = new TitledBorder("History");
        historyTitleBorder.setTitleJustification(TitledBorder.CENTER);
        historyTitleBorder.setTitlePosition(TitledBorder.TOP);
        history = new JList(historyModel);
        JScrollPane historyPane = new JScrollPane(history);
        historyPane.setPreferredSize(new Dimension(180, 300));
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

    private void initializeMenu() {
        mainWindowMenu = new MainWindowMenu(this);
        setJMenuBar(mainWindowMenu.initMenuComponents());
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
            int index = headingsList.getSelectedIndex();
            if (index == -1) {
                return;
            }
            dictionaryPane.moveTo(index);
            headingsList.clearSelection();
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

        dictionaryInfoList.addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) {
                // The user is still manipulating the selection.
                return;
            }
            int[] indecs = dictionaryInfoList.getSelectedIndices();
            selectedDicts.clear();
            for (int idx: indecs) {
                String dictName = dictionaryInfoModel.get(idx);
                selectedDicts.add(dictName);
            }
            updateResult();
        });
    }
}
