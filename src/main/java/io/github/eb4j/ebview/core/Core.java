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

package io.github.eb4j.ebview.core;

import io.github.eb4j.ebview.data.DictionaryEntry;
import io.github.eb4j.ebview.dictionary.DictionariesManager;
import io.github.eb4j.ebview.gui.IMainWindow;
import io.github.eb4j.ebview.gui.MainWindow;
import io.github.eb4j.ebview.gui.MainWindowMenu;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import java.awt.Font;
import java.io.File;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Core {

    private static final List<IFontChangedListener> FONT_CHANGED_EVENT_LISTENERS = new CopyOnWriteArrayList<>();
    private static DictionariesManager dictionariesManager;
    private static MainWindow mw;

    private Core() {
    }

    public static void initializeGUI(final File dictionaryDirectory, final boolean remote) {
        dictionariesManager = new DictionariesManager();
        mw = new MainWindow();
        if (dictionaryDirectory != null) {
            dictionariesManager.loadDictionaries(dictionaryDirectory);
        }
        if (!remote) {
            new MainWindowMenu();
            mw.showMessage("Please add dictionaries from Dictionary menu at first.");
        } else {
            mw.showMessage("Please enter search word above input box.");
        }
    }

    public static void run() {
        mw.setVisible(true);
    }

    public static IMainWindow getMainWindow() {
        return mw;
    }
    public static DictionariesManager getDictionariesManager() {
        return dictionariesManager;
    }

    public static JFrame getApplicationFrame() {
        return mw.getApplicationFrame();
    }

    /** Register listener. */
    public static void registerFontChangedEventListener(final IFontChangedListener listener) {
        FONT_CHANGED_EVENT_LISTENERS.add(listener);
    }

    /** Unregister listener. */
    public static void unregisterFontChangedEventListener(final IFontChangedListener listener) {
        FONT_CHANGED_EVENT_LISTENERS.remove(listener);
    }

    /** Fire event. */
    public static void fireFontChanged(final Font newFont) {
        SwingUtilities.invokeLater(() -> {
            for (IFontChangedListener listener : FONT_CHANGED_EVENT_LISTENERS) {
                try {
                    listener.onFontChanged(newFont);
                } catch (Throwable ignored) {
                }
            }
        });
    }

    public static void updateDictionaryPane(final List<DictionaryEntry> entries) {
        mw.updateDictionaryPane(entries);
    }

    public static void moveTo(final int index) {
        mw.moveTo(index);
    }
}
