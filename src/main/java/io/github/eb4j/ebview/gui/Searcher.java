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
import io.github.eb4j.ebview.dictionary.DictionariesManager;

import javax.swing.SwingWorker;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Searcher worker.
 *
 * @author Hiroshi Miura
 */
class Searcher extends SwingWorker<Object, Object> {
    private final EBViewerModel ebViewerModel;
    private final DictionariesManager manager;

    Searcher(final EBViewerModel model, final DictionariesManager manager) {
        super();
        this.manager = manager;
        ebViewerModel = model;
    }

    /**
     * Computes a result, or throws an exception if unable to do so.
     *
     * @return the computed result
     */
    @Override
    protected Object doInBackground() {
        String word = ebViewerModel.getSearchWord();
        new Thread(() -> {
            try {
                List<DictionaryEntry> result = manager.findWord(word);
                ebViewerModel.addToHistory(word);
                publish(result);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void process(final List<Object> chunks) {
        super.process(chunks);
        for (Object obj : chunks) {
            List<DictionaryEntry> entries = (List<DictionaryEntry>) obj;
            List<String> dictList = entries.stream()
                    .map(DictionaryEntry::getDictName)
                    .distinct()
                    .collect(Collectors.toList());
            ebViewerModel.setDictionaryList(dictList);
            ebViewerModel.updateResult(entries);
        }
    }

}
