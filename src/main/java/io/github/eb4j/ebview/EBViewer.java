package io.github.eb4j.ebview;

import com.formdev.flatlaf.FlatLightLaf;
import io.github.eb4j.ebview.dictionary.DictionariesManager;
import io.github.eb4j.ebview.gui.MainWindow;

import java.io.File;

public class EBViewer implements Runnable {

    private final DictionariesManager dictionariesManager;

    public EBViewer(final File dictionaryDirectory) {
        dictionariesManager = new DictionariesManager();
        if (dictionaryDirectory != null) {
            dictionariesManager.loadDictionaries(dictionaryDirectory);
        }
    }

    public DictionariesManager getDictionariesManager() {
        return dictionariesManager;
    }

    /**
     * Main function.
     * @param args command line arguments.
     */
    public static void main(final String... args) {
        io.github.eb4j.ebview.protocol.data.Handler.install();
        FlatLightLaf.setup();
        File dictionaryDirectory = null;
        if (args.length == 1) {
            dictionaryDirectory = new File(args[0]);
        }
        try {
            EBViewer viewer = new EBViewer(dictionaryDirectory);
            Thread t = new Thread(viewer);
            t.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        new MainWindow(dictionariesManager);
    }
}
