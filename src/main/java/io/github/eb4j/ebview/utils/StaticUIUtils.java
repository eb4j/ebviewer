/*
 * EBViewer, a dictionary viewer application.
 * Copyright (C) 2022 Hiroshi Miura.
 * Copyright (C) 2006 Henry Pijffers
 *               2013 Yu Tang
 *               2014-2015 Aaron Madlon-Kay
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

package io.github.eb4j.ebview.utils;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Henry Pijffers
 * @author Yu-Tang
 * @author Aaron Madlon-Kay
 */
public final class StaticUIUtils {

    private StaticUIUtils() {
    }

    private static final KeyStroke ESC_KEYSTROKE = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false);

    /**
     * Make a dialog closeable by pressing the Esc key.
     * {@link JDialog#dispose()} will be called.
     *
     * @param dialog
     */
    public static void setEscapeClosable(final JDialog dialog) {
        setEscapeAction(dialog.getRootPane(), makeCloseAction(dialog));
    }

    @SuppressWarnings("serial")
    public static Action makeCloseAction(final Window window) {
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                closeWindowByEvent(window);
            }
        };
    }

    /**
     * Send a {@link WindowEvent#WINDOW_CLOSING} event to the supplied window.
     * This mimics closing by clicking the window close button.
     */
    public static void closeWindowByEvent(final Window window) {
        window.dispatchEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSING));
    }

    /**
     * Associate a custom action to be called when the Esc key is pressed.
     *
     * @param pane
     * @param action
     */
    public static void setEscapeAction(final JRootPane pane, final Action action) {
        // Handle escape key to close the window
        pane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(ESC_KEYSTROKE, "ESCAPE");
        pane.getActionMap().put("ESCAPE", action);
    }

    public static <T> T returnResultFromSwingThread(final Supplier<T> s) {
        if (SwingUtilities.isEventDispatchThread()) {
            return s.get();
        } else {
            AtomicReference<T> reference = new AtomicReference<>();
            try {
                SwingUtilities.invokeAndWait(() -> reference.set(s.get()));
            } catch (InvocationTargetException | InterruptedException e) {
                Logger.getLogger(StaticUIUtils.class.getName()).log(Level.WARNING, e.getLocalizedMessage(),
                        e);
            }
            return reference.get();
        }
    }
}
