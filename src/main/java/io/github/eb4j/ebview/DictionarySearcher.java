package io.github.eb4j.ebview;

import javax.swing.*;
import java.util.List;

public class DictionarySearcher extends Thread {

    private ThreadPane pane;
    private EBDict ebDict;

    private String word;

    /**
     * Allocates a new {@code Thread} object. This constructor has the same
     * effect as {@linkplain #Thread(ThreadGroup, Runnable, String) Thread}
     * {@code (null, null, gname)}, where {@code gname} is a newly generated
     * name. Automatically generated names are of the form
     * {@code "Thread-"+}<i>n</i>, where <i>n</i> is an integer.
     */
    public DictionarySearcher(ThreadPane pane, EBDict ebDict) {
        this.pane = pane;
        this.ebDict = ebDict;
    }

    @Override
    public void run() {
        List<DictionaryEntry> result = ebDict.readArticles(word);
        SwingUtilities.invokeLater(() -> pane.setFoundResult(result));
    }

    public void setWord(final String word) {
        this.word = word;
    }
}
