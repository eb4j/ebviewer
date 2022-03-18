/*
 * EBViewer, a dictionary viewer application.
 * Copyright (C) 2022 Hiroshi Miura.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.github.eb4j.ebview;

import com.formdev.flatlaf.FlatLightLaf;
import io.github.eb4j.ebview.core.Core;
import io.github.eb4j.ebview.dictionary.DictionariesManager;
import io.github.eb4j.ebview.gui.MainWindow;
import io.github.eb4j.ebview.gui.MainWindowMenu;
import io.github.eb4j.ebview.utils.Preferences;
import org.jetbrains.projector.server.ProjectorLauncher;
import org.jetbrains.projector.server.ProjectorServer;
import tokyo.northside.protocol.URLProtocolHandler;

import javax.swing.JFrame;
import java.io.File;

public class EBViewer implements Runnable {

    public EBViewer() {
    }

    /**
     * Main function.
     * @param args command line arguments.
     */
    public static void main(final String... args) {
        boolean remote = ProjectorServer.isEnabled();
        if (remote) {
            if (!ProjectorLauncher.runProjectorServer()) {
                throw new RuntimeException("Fail to start projector server");
            }
        }
        URLProtocolHandler.install();
        FlatLightLaf.setup();
        File dictionaryDirectory = null;
        if (args.length == 1) {
            dictionaryDirectory = new File(args[0]);
        }
        try {
            Preferences.init();
            Core.initializeGUI(dictionaryDirectory, remote);
            EBViewer viewer = new EBViewer();
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
        Core.run();
    }
}
