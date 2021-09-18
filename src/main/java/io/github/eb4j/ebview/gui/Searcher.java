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
