package io.github.eb4j.ebview.gui;

import io.github.eb4j.ebview.data.DictionaryEntry;
import io.github.eb4j.ebview.dictionary.DictionariesManager;

import javax.swing.BoxLayout;
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
import java.awt.Window;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.List;

import static io.github.eb4j.ebview.utils.ResourceUtil.APP_ICON_16X16;
import static io.github.eb4j.ebview.utils.ResourceUtil.APP_ICON_32X32;

/**
 * Swing main window.
 * @author Hiroshi Miura
 */
public final class MainWindow extends JFrame implements IMainWindow {
    private final JButton searchButton = new JButton();
    private final BasicArrowButton zoomUpButton = new BasicArrowButton(BasicArrowButton.NORTH);
    private final BasicArrowButton zoomDownButton = new BasicArrowButton(BasicArrowButton.SOUTH);

    private final DictionariesManager dictionariesManager;
    private final EBViewerModel ebViewerModel;

    private static JList<String> dictionaryInfoList;
    private static final DictionaryPane DICTIONARY_PANE = new DictionaryPane();

    private static final JTextField SEARCH_WORD_FIELD = new JTextField();

    private JLabel zoomLevel;
    private JLabel selectAllDictionary;
    private JList<String> headingsList;
    private JList<String> history;

    public MainWindow(final DictionariesManager dictionariesManager) {
        super("EBViewer");
        this.dictionariesManager = dictionariesManager;
        ebViewerModel = new EBViewerModel();
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


    public static String getSearchWord() {
        return SEARCH_WORD_FIELD.getText();
    }

    public static void setMessage(final String message) {
        DICTIONARY_PANE.setText(message);
    }

    @Override
    public DictionariesManager getDictionariesManager() {
        return dictionariesManager;
    }

    @Override
    public JFrame getApplicationFrame() {
        return this;
    }

    public static void updateDictionaryPane(final List<DictionaryEntry> entries) {
        DICTIONARY_PANE.setFoundResult(entries);
        DICTIONARY_PANE.setCaretPosition(0);
    }

    public static void updateDictionaryList(final int[] indecs) {
        dictionaryInfoList.setSelectedIndices(indecs);
    }

    private void initializeGUI() {
        setPreferredSize(new Dimension(800, 500));
        setLayout(new BorderLayout());
        //
        selectAllDictionary = new JLabel("Select all");
        dictionaryInfoList = new JList<>(ebViewerModel.getDictionaryInfoModel());
        JScrollPane dictionaryInfoPane = new JScrollPane(dictionaryInfoList);
        dictionaryInfoPane.setPreferredSize(new Dimension(180, 80));
        //
        // GUI parts
        JPanel panel1 = new JPanel();
        JLabel zoomLabel = new JLabel("zoom:");
        zoomLevel = new JLabel();
        zoomLevel.setText(DICTIONARY_PANE.getZoomLevel());
        panel1.setLayout(new FlowLayout());
        SEARCH_WORD_FIELD.setPreferredSize(new Dimension(500, 30));
        searchButton.setText("Search");
        panel1.add(SEARCH_WORD_FIELD);
        panel1.add(searchButton);
        panel1.add(zoomLabel);
        panel1.add(zoomDownButton);
        panel1.add(zoomLevel);
        panel1.add(zoomUpButton);
        //
        headingsList = new JList<>(ebViewerModel.getHeadingsModel());
        JScrollPane headingsPane = new JScrollPane(headingsList);
        headingsPane.setPreferredSize(new Dimension(140, -1));
        //
        JScrollPane articlePane = new JScrollPane(DICTIONARY_PANE);
        articlePane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        //
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.PAGE_AXIS));
        //
        TitledBorder historyTitleBorder = new TitledBorder("History");
        historyTitleBorder.setTitleJustification(TitledBorder.CENTER);
        historyTitleBorder.setTitlePosition(TitledBorder.TOP);
        history = new JList<>(ebViewerModel.getHistoryModel());
        JScrollPane historyPane = new JScrollPane(history);
        historyPane.setPreferredSize(new Dimension(180, 300));
        historyPane.setBorder(historyTitleBorder);
        infoPanel.add(selectAllDictionary);
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

    public void showMessage(final String msg) {
        DICTIONARY_PANE.setText(msg);
    }

    private void startSearch() {
        Searcher searchSwingWorker = new Searcher(ebViewerModel, dictionariesManager);
        searchSwingWorker.execute();
    }

    private void setActions() {
        SEARCH_WORD_FIELD.addActionListener(e -> {
            startSearch();
        });

        searchButton.addActionListener(e -> {
            startSearch();
        });

        zoomDownButton.addActionListener(e -> {
            DICTIONARY_PANE.decreaseZoom();
            zoomLevel.setText(DICTIONARY_PANE.getZoomLevel());
        });

        zoomUpButton.addActionListener(e -> {
            DICTIONARY_PANE.increaseZoom();
            zoomLevel.setText(DICTIONARY_PANE.getZoomLevel());
        });

        headingsList.addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) {
                return;
            }
            int index = headingsList.getSelectedIndex();
            if (index == -1) {
                return;
            }
            DICTIONARY_PANE.moveTo(index);
            headingsList.clearSelection();
        });

        history.addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) {
                // The user is still manipulating the selection.
                return;
            }
            Object obj = history.getSelectedValue();
            if (obj != null) {
                SEARCH_WORD_FIELD.setText(obj.toString());
            }
        });

        // catch double-click events
        history.addMouseListener(new MouseAdapter() {
            public void mouseClicked(final MouseEvent me) {
                if (me.getClickCount() == 2) {
                    Object obj = history.getSelectedValue();
                    if (obj != null) {
                        SEARCH_WORD_FIELD.setText(obj.toString());
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
                        SEARCH_WORD_FIELD.setText(obj.toString());
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
            ebViewerModel.selectDicts(indecs);
        });

        selectAllDictionary.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                ebViewerModel.selectAllDict();
            }
        });
    }
}
