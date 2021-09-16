package io.github.eb4j.ebview.gui;

import io.github.eb4j.ebview.data.DictionaryEntry;
import io.github.eb4j.ebview.dictionary.DictionariesManager;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.basic.BasicArrowButton;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.SystemTray;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
    private final DefaultListModel<String> dictionaryInfoModel = new DefaultListModel<>();
    private final JButton searchButton = new JButton();
    private final BasicArrowButton zoomUpButton = new BasicArrowButton(BasicArrowButton.NORTH);
    private final BasicArrowButton zoomDownButton = new BasicArrowButton(BasicArrowButton.SOUTH);

    private final DictionariesManager dictionariesManager;

    private JTextField searchWordField;
    private DefaultListModel<String> headingsModel;
    private DictionaryPane dictionaryPane;
    private JLabel zoomLevel;

    private JList<String> headingsList;
    private JList<String> history;
    private JList<String> dictionaryInfoList;

    public MainWindow(final DictionariesManager dictionariesManager) {
        super("EBViewer");
        this.dictionariesManager = dictionariesManager;
        // Set X11 application class name to make some desktop user interfaces
        // (like Gnome Shell) recognize
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Class<?> cls = toolkit.getClass();
        if (cls.getName().equals("sun.awt.X11.XToolkit")) {
            try {
                Field field = cls.getDeclaredField("awtAppClassName");
                field.setAccessible(true);
                field.set(toolkit, "EBViewer");
            } catch (NoSuchFieldException | IllegalAccessException ignored) {
            }
        }
        setWindowIcon(this);

        initializeGUI();
        setActions();
        pack();
        setResizable(true);
    }

    public static void setWindowIcon(final Window window) {
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
        dictionaryPane = new DictionaryPane();
        dictionaryInfoList = new JList<>(dictionaryInfoModel);
        JScrollPane dictionaryInfoPane = new JScrollPane(dictionaryInfoList);
        dictionaryInfoPane.setPreferredSize(new Dimension(180, 80));
        //
        // GUI parts
        JPanel panel1 = new JPanel();
        JLabel zoomLabel = new JLabel("zoom:");
        zoomLevel = new JLabel();
        zoomLevel.setText(dictionaryPane.getZoomLevel());
        panel1.setLayout(new FlowLayout());
        searchWordField = new JTextField();
        searchWordField.setPreferredSize(new Dimension(500, 30));
        searchButton.setText("Search");
        panel1.add(searchWordField);
        panel1.add(searchButton);
        panel1.add(zoomLabel);
        panel1.add(zoomDownButton);
        panel1.add(zoomLevel);
        panel1.add(zoomUpButton);
        //
        headingsModel = new DefaultListModel<>();
        headingsList = new JList<>(headingsModel);
        JScrollPane headingsPane = new JScrollPane(headingsList);
        headingsPane.setPreferredSize(new Dimension(140, -1));
        //
        JScrollPane articlePane = new JScrollPane(dictionaryPane);
        articlePane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        //
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.PAGE_AXIS));
        //
        TitledBorder historyTitleBorder = new TitledBorder("History");
        historyTitleBorder.setTitleJustification(TitledBorder.CENTER);
        historyTitleBorder.setTitlePosition(TitledBorder.TOP);
        history = new JList<>(historyModel);
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
        if (SystemTray.isSupported()) {
            setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        } else {
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        }
    }

    private void startSearch() {
        Searcher searchSwingWorker = new Searcher(this);
        searchSwingWorker.execute();
    }

    private void setActions() {
        searchWordField.addActionListener(e -> {
            startSearch();
        });

        searchButton.addActionListener(e -> {
            startSearch();
        });

        zoomDownButton.addActionListener(e -> {
            dictionaryPane.decreaseZoom();
            zoomLevel.setText(dictionaryPane.getZoomLevel());
        });

        zoomUpButton.addActionListener(e -> {
            dictionaryPane.increaseZoom();
            zoomLevel.setText(dictionaryPane.getZoomLevel());
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

        // catch double-click events
        history.addMouseListener(new MouseAdapter() {
            public void mouseClicked(final MouseEvent me) {
                if (me.getClickCount() == 2) {
                    Object obj = history.getSelectedValue();
                    if (obj != null) {
                        searchWordField.setText(obj.toString());
                        startSearch();
                    }
                }
            }
        });

        // catch enter-key events
        history.addKeyListener(new KeyAdapter() {
            public void keyReleased(final KeyEvent ke) {
                if (ke.getKeyCode() == KeyEvent.VK_ENTER) {
                    Object obj = history.getSelectedValue();
                    if (obj != null) {
                        searchWordField.setText(obj.toString());
                        startSearch();
                    }
                }
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
