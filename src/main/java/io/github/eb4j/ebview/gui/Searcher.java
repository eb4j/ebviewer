package io.github.eb4j.ebview.gui;

import io.github.eb4j.ebview.data.DictionaryEntry;

import javax.swing.SwingWorker;
import java.util.List;

/**
 * Searcher worker.
 *
 * @author Hiroshi Miura
 */
class Searcher extends SwingWorker<Object, Object> {
    private final MainWindow mainWindow;

    public Searcher(MainWindow mainWindow) {
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
        String word = mainWindow.searchWordField.getText();
        mainWindow.historyModel.add(0, word);
        mainWindow.headingsModel.removeAllElements();
        new Thread(() -> {
            try {
                List<DictionaryEntry> result = mainWindow.dictionariesManager.findWord(word);
                publish(result);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
        return null;
    }

    @Override
    protected void process(List<Object> chunks) {
        super.process(chunks);
        for (Object obj : chunks) {
            mainWindow.setResult((List<DictionaryEntry>) obj);
        }
    }

}
