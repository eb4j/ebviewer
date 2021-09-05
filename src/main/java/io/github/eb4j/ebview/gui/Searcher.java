package io.github.eb4j.ebview.gui;

import io.github.eb4j.ebview.data.DictionaryEntry;
import io.github.eb4j.ebview.data.IDictionary;

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
        for (IDictionary dictionary : mainWindow.dictionaries) {
            new Thread(() -> {
                List<DictionaryEntry> result = null;
                try {
                    result = dictionary.readArticlesPredictive(word);
                    publish(result);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
        return null;
    }

    @Override
    protected void process(List<Object> chunks) {
        super.process(chunks);
        for (Object obj : chunks) {
            List<DictionaryEntry> result = (List<DictionaryEntry>) obj;
            mainWindow.headingsModel.addAll(result.stream().map(DictionaryEntry::getWord).collect(Collectors.toList()));
            mainWindow.dictionaryPane.setFoundResult(result);
            mainWindow.dictionaryPane.setCaretPosition(0);
        }
    }

    protected void done() {

    }
}
