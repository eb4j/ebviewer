package io.github.eb4j.ebview;

import io.github.eb4j.ebview.dictionary.DictionariesManager;
import io.github.eb4j.ebview.gui.MainWindow;
import io.github.eb4j.ebview.protocol.data.Handler;
import io.github.eb4j.ebview.utils.FileUtils;

import java.io.File;
import java.util.List;

public class EBViewer implements Runnable {

    private DictionariesManager dictionariesManager;

    public EBViewer(final File dictionaryDirectory) {
        dictionariesManager = new DictionariesManager();
        List<File> listFiles = FileUtils.findFiles(dictionaryDirectory);
        for (File f: listFiles) {
            try {
                dictionariesManager.loadDictionary(f);
            } catch (Exception ignore) {
            }
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
        if (args.length < 1) {
            System.exit(1);
        }
        File dictionaryDirectory = new File(args[0]);

        if (!dictionaryDirectory.isDirectory()) {
            System.err.println("Path is not a directory.");
            System.exit(1);
        }

        Handler.install();

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
