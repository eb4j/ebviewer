package io.github.eb4j.ebview;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class EBViewer implements Runnable {

    // dictionaries to use
    private final Set<EBDict> dictionaries = new HashSet<>();

    public EBViewer(final File dict) throws Exception {
        dictionaries.add(new EBDict(dict));
    }

    /**
     * Main function.
     * @param args command line arguments.
     */
    public static void main(final String... args) {
        if (args.length < 1) {
            System.exit(1);
        }
        File dict = new File(args[0]);
        try {
            EBViewer viewer = new EBViewer(dict);
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
        new MainWindow(this);
    }

    public Set<EBDict> getDictionaries() {
        return Collections.unmodifiableSet(dictionaries);
    }
}
