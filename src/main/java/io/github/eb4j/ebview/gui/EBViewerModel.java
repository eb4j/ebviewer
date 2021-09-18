package io.github.eb4j.ebview.gui;

import io.github.eb4j.ebview.data.DictionaryEntry;

import javax.swing.DefaultListModel;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class EBViewerModel {

    private static final DefaultListModel<String> dictionaryInfoModel = new DefaultListModel<>();
    private static final DefaultListModel<String> historyModel = new DefaultListModel<>();
    private static final DefaultListModel<String> headingsModel = new DefaultListModel<>();

    private final Set<String> selectedDicts = new HashSet<>();
    private final List<DictionaryEntry> ourResult = new ArrayList<>();

    public EBViewerModel() {
    }

    DefaultListModel<String> getDictionaryInfoModel() {
        return dictionaryInfoModel;
    }

    DefaultListModel<String> getHeadingsModel() {
        return headingsModel;
    }

    DefaultListModel<String> getHistoryModel() {
        return historyModel;
    }

    void selectAllDict() {
        selectedDicts.clear();
        int[] indecs = new int[dictionaryInfoModel.getSize()];
        for (int i = 0; i < dictionaryInfoModel.getSize(); i++) {
            indecs[i] = i;
            selectedDicts.add(dictionaryInfoModel.get(i));
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
            String dictName = dictionaryInfoModel.get(idx);
            selectedDicts.add(dictName);
        }
        updateResult();
    }

    void setDictionaryList(final List<String> dictList) {
        dictionaryInfoModel.removeAllElements();
        dictionaryInfoModel.addAll(dictList);
        selectedDicts.addAll(dictList);
    }

    void addToHistory(final String word) {
        historyModel.add(0, word);
    }

    void updateHeadingsModel(final List<String> wordList) {
        headingsModel.removeAllElements();
        headingsModel.addAll(wordList);
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
