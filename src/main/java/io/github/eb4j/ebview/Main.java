package io.github.eb4j.ebview;

import java.io.File;

/**
 * Main class for main command.
 */
public final class Main {

    private Main() {
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
            EBDict ebDict = new EBDict(dict);
            new MainWindow(ebDict);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
