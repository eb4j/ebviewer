package io.github.eb4j.ebview.gui;

import io.github.eb4j.ebview.data.DictionaryEntry;

import javax.swing.SwingWorker;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Searcher worker.
 *
 * @author Hiroshi Miura
 */
class Searcher extends SwingWorker<Object, Object> {
    private final MainWindow mainWindow;

    Searcher(final MainWindow mainWindow) {
        super();
        this.mainWindow = mainWindow;
    }

    /**
     * Computes a result, or throws an exception if unable to do so.
     *
     * @return the computed result
     */
    @Override
    protected Object doInBackground() {
        String word = mainWindow.getSearchWord();
        new Thread(() -> {
            try {
                List<DictionaryEntry> result = mainWindow.getDictionariesManager().findWord(word);
                mainWindow.addToHistory(word);
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
            List<String> dictList = entries.stream().map(DictionaryEntry::getDictName).distinct().collect(Collectors.toList());
            mainWindow.setDictionaryList(dictList);
            mainWindow.updateResult(entries);
        }
    }

}
