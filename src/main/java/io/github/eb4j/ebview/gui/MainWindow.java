/*
 * EBViewer, a dictionary viewer application.
 * Copyright (C) 2022 Hiroshi Miura.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.github.eb4j.ebview.gui;

import io.github.eb4j.ebview.core.Core;
import io.github.eb4j.ebview.data.DictionaryEntry;
import io.github.eb4j.ebview.utils.Preferences;

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
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
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

    private final EBViewerModel ebViewerModel;

    private static JList<String> dictionaryInfoList;

    private static final JTextField SEARCH_WORD_FIELD = new JTextField();

    private final DictionaryPane dictionaryPane;

    private JLabel selectAllDictionary;
    private JList<String> headingsList;
    private JList<String> history;

    private final Font font;

    public MainWindow() {
        super("EBViewer");
        ebViewerModel = new EBViewerModel();
        String fontName = Preferences.getPreferenceDefault(Preferences.APPEARANCE_FONT_NAME,
                Preferences.APPEARANCE_FONT_DEFAULT);
        int fontSize = Preferences.getPreferenceDefault(Preferences.APPEARANCE_FONT_SIZE,
                Preferences.APPEARANCE_FONT_SIZE_DEFAULT);
        font = new Font(fontName, Font.PLAIN, fontSize);
        setFont(font);
        setWindowIcon(this);
        dictionaryPane = new DictionaryPane(font);
        initializeGUI();
        Core.registerFontChangedEventListener(this::setFont);
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

    @Override
    public JFrame getApplicationFrame() {
        return this;
    }

    @Override
    public Font getApplicationFont() {
        return font;
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
        panel1.setLayout(new FlowLayout());
        SEARCH_WORD_FIELD.setPreferredSize(new Dimension(500, 30));
        searchButton.setText("Search");
        panel1.add(SEARCH_WORD_FIELD);
        panel1.add(searchButton);
        //
        headingsList = new JList<>(ebViewerModel.getHeadingsModel());
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
        dictionaryPane.setText(msg);
    }

    private void startSearch() {
        Searcher searchSwingWorker = new Searcher(ebViewerModel, Core.getDictionariesManager());
        searchSwingWorker.execute();
    }

    private void setActions() {
        SEARCH_WORD_FIELD.addActionListener(e -> {
            startSearch();
        });

        searchButton.addActionListener(e -> {
            startSearch();
        });

        headingsList.addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) {
                return;
            }
            int index = headingsList.getSelectedIndex();
            if (index == -1) {
                return;
            }
            Core.moveTo(index);
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
            public void mouseClicked(final MouseEvent e) {
                ebViewerModel.selectAllDict();
            }
        });
    }

    public void updateDictionaryPane(final List<DictionaryEntry> entries) {
        dictionaryPane.setFoundResult(entries);
        dictionaryPane.setCaretPosition(0);
    }

    public void moveTo(final int index) {
        dictionaryPane.moveTo(index);
    }
}
