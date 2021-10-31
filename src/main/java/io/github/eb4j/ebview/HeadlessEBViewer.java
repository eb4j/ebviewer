package io.github.eb4j.ebview;

import org.jetbrains.projector.server.ProjectorLauncher;
import org.jetbrains.projector.server.ProjectorServer;
import tokyo.northside.protocol.URLProtocolHandler;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import java.io.File;

public class HeadlessEBViewer extends EBViewer implements Runnable {


    public HeadlessEBViewer(final File dictionaryDirectory) {
        super(dictionaryDirectory);
    }

    /**
     * Main function.
     * @param args command line arguments.
     */
    public static void main(final String... args) {
        if (ProjectorServer.isEnabled()) {
            if (!ProjectorLauncher.runProjectorServer()) {
                throw new RuntimeException("Fail to start projector server");
            }
        }
        URLProtocolHandler.install();
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (ClassNotFoundException | UnsupportedLookAndFeelException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
        File dictionaryDirectory = null;
        if (args.length == 1) {
            dictionaryDirectory = new File(args[0]);
        }
        try {
            EBViewer viewer = new HeadlessEBViewer(dictionaryDirectory);
            Thread t = new Thread(viewer);
            t.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
