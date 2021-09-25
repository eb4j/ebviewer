package io.github.eb4j.ebview;

import com.formdev.flatlaf.FlatLightLaf;
import io.github.eb4j.ebview.dictionary.DictionariesManager;
import io.github.eb4j.ebview.gui.MainWindow;
import io.github.eb4j.ebview.gui.MainWindowMenu;
import tokyo.northside.protocol.URLProtocolHandler;

import javax.swing.JFrame;
import java.io.File;

public class EBViewer implements Runnable {

    private final DictionariesManager dictionariesManager;
    private final MainWindow mw;

    public EBViewer(final File dictionaryDirectory) {
        dictionariesManager = new DictionariesManager();
        if (dictionaryDirectory != null) {
            dictionariesManager.loadDictionaries(dictionaryDirectory);
        }
        mw = new MainWindow(dictionariesManager);
        new MainWindowMenu(this);
    }

    public DictionariesManager getDictionariesManager() {
        return dictionariesManager;
    }

    public JFrame getApplicationFrame() {
        return mw.getApplicationFrame();
    }

    /**
     * Main function.
     * @param args command line arguments.
     */
    public static void main(final String... args) {
        URLProtocolHandler.install();
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
        mw.setVisible(true);
    }
}
