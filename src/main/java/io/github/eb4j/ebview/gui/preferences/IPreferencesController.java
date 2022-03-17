/*
 * EBViewer, a dictionary viewer application.
 * Copyright (C) 2022 Hiroshi Miura.
 *               2016 Aaron Madlon-Kay
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

package io.github.eb4j.ebview.gui.preferences;

import java.awt.Component;

/**
 * An interface implemented by views shown in the Preferences window. See
 * <code>BasePreferencesController</code> for a base implementation.
 *
 * @author Aaron Madlon-Kay
 *
 */
public interface IPreferencesController {

    /**
     * Implementors should override this to return the name of the view as shown in the view tree.
     */
    String toString();

    /**
     * Get the GUI (the "view") controlled by this controller. This should not
     * be a window (e.g. JDialog, JFrame) but rather a component embeddable in a
     * window (e.g. JPanel).
     */
    Component getGui();

    /**
     * Get the parent view in the view tree. Implementors should override this
     * to return the class of the desired parent; by default this is the Plugins
     * view.
     */
    default Class<? extends IPreferencesController> getParentViewClass() {
        return null;
    }

    /**
     * Commit changes.
     */
    void persist();

    /**
     * Validate the current preferences. Implementors should override to
     * implement validation logic as necessary.
     * <p>
     * When validation fails, implementors should <i>not</i> raise dialogs;
     * instead they should offer feedback within the view GUI.
     *
     * @return True if the settings are valid and OK to be persisted; false if
     *         not
     */
    default boolean validate() {
        return true;
    }

    /**
     * Restore preferences controlled by this view to their current persisted
     * state.
     */
    void undoChanges();

    /**
     * Restore preferences controlled by this view to their default state.
     */
    void restoreDefaults();
}
