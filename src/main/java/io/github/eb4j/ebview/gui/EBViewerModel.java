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

import io.github.eb4j.ebview.data.DictionaryEntry;

import javax.swing.DefaultListModel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class EBViewerModel {

    private static final DefaultListModel<String> DICTIONARY_INFO_MODEL = new DefaultListModel<>();
    private static final DefaultListModel<String> HISTORY_MODEL = new DefaultListModel<>();
    private static final DefaultListModel<String> HEADINGS_MODEL = new DefaultListModel<>();

    private final Set<String> selectedDicts = new HashSet<>();
    private final List<DictionaryEntry> ourResult = new ArrayList<>();

    public EBViewerModel() {
    }

    DefaultListModel<String> getDictionaryInfoModel() {
        return DICTIONARY_INFO_MODEL;
    }

    DefaultListModel<String> getHeadingsModel() {
        return HEADINGS_MODEL;
    }

    DefaultListModel<String> getHistoryModel() {
        return HISTORY_MODEL;
    }

    void selectAllDict() {
        selectedDicts.clear();
        int[] indecs = new int[DICTIONARY_INFO_MODEL.getSize()];
        for (int i = 0; i < DICTIONARY_INFO_MODEL.getSize(); i++) {
            indecs[i] = i;
            selectedDicts.add(DICTIONARY_INFO_MODEL.get(i));
        }
        MainWindow.updateDictionaryList(indecs);
        updateResult();
    }

    String getSearchWord() {
        return MainWindow.getSearchWord();
    }

    Set<String> getSelectedDicts() {
        return Collections.unmodifiableSet(selectedDicts);
    }

    void selectDicts(final int[] indecs) {
        selectedDicts.clear();
        for (int idx: indecs) {
            String dictName = DICTIONARY_INFO_MODEL.get(idx);
            selectedDicts.add(dictName);
        }
        updateResult();
    }

    void setDictionaryList(final List<String> dictList) {
        DICTIONARY_INFO_MODEL.removeAllElements();
        DICTIONARY_INFO_MODEL.addAll(dictList);
        selectedDicts.addAll(dictList);
    }

    void addToHistory(final String word) {
        HISTORY_MODEL.add(0, word);
    }

    void updateHeadingsModel(final List<String> wordList) {
        HEADINGS_MODEL.removeAllElements();
        HEADINGS_MODEL.addAll(wordList);
    }

    public void updateResult(final List<DictionaryEntry> result) {
        ourResult.clear();
        ourResult.addAll(result);
        updateResult();
    }

    public void updateResult() {
        List<String> wordList = new ArrayList<>();
        List<DictionaryEntry> entries = new ArrayList<>();
        Set<String> dicts = getSelectedDicts();
        for (DictionaryEntry dictionaryEntry : ourResult) {
            String name = dictionaryEntry.getDictName();
            if (dicts.contains(name)) {
                entries.add(dictionaryEntry);
                wordList.add(String.format("<html><span style='font-style: italic'>%s</span>&nbsp;&nbsp;%s</html>",
                        name.substring(0, 2), dictionaryEntry.getWord()));
            }
        }
        updateHeadingsModel(wordList);
        MainWindow.updateDictionaryPane(entries);
    }

}
