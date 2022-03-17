/**************************************************************************
 OmegaT - Computer Assisted Translation (CAT) tool
          with fuzzy matching, translation memory, keyword search,
          glossaries, and translation leveraging into updated projects.

 Copyright (C) 2016 Aaron Madlon-Kay
               Home page: http://www.omegat.org/
               Support center: https://omegat.org/support

 This file is part of OmegaT.

 OmegaT is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 OmegaT is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 **************************************************************************/

package io.github.eb4j.ebview.gui.preferences;

import io.github.eb4j.ebview.utils.CredentialsManager;
import io.github.eb4j.ebview.utils.LStrings;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import java.awt.event.HierarchyEvent;

/**
 * @author Aaron Madlon-Kay
 */
public class SecureStoreController implements IPreferencesController {

    private SecureStorePanel panel;

    public JComponent getGui() {
        if (panel == null) {
            initGui();
            initFromPrefs();
        }
        return panel;
    }

    public String toString() {
        return LStrings.getString("PREFS_TITLE_SECURE_STORE");
    }

    private void initGui() {
        panel = new SecureStorePanel();
        panel.resetPasswordButton.addActionListener(e -> resetMasterPassword());
        panel.addHierarchyListener(e -> {
            if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0) {
                updateMasterPasswordStatus();
            }
        });
    }

    private void resetMasterPassword() {
        if (JOptionPane.OK_OPTION == JOptionPane.showConfirmDialog(SwingUtilities.windowForComponent(panel),
                LStrings.getString("PREFS_SECURE_STORAGE_RESET_MASTER_PASSWORD_MESSAGE"),
                LStrings.getString("PREFS_SECURE_STORAGE_RESET_MASTER_PASSWORD_TITLE"),
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE)) {
            CredentialsManager.getInstance().clearMasterPassword();
            updateMasterPasswordStatus();
        }
    }

    protected void initFromPrefs() {
        updateMasterPasswordStatus();
    }

    private void updateMasterPasswordStatus() {
        boolean isSet = CredentialsManager.getInstance().isMasterPasswordSet();
        boolean isStored = CredentialsManager.getInstance().isMasterPasswordStored();
        String status;
        if (isSet && isStored) {
            status = LStrings.getString("PREFS_SECURE_STORAGE_MASTER_PASSWORD_SET_STORED");
        } else if (isSet) {
            status = LStrings.getString("PREFS_SECURE_STORAGE_MASTER_PASSWORD_SET");
        } else {
            status = LStrings.getString("PREFS_SECURE_STORAGE_MASTER_PASSWORD_NOT_SET");
        }
        panel.masterPasswordStatusLabel.setText(status);
        panel.resetPasswordButton.setEnabled(isSet);
    }

    public void restoreDefaults() {
    }

    public void persist() {
    }

    /**
     * Restore preferences controlled by this view to their current persisted
     * state.
     */
    @Override
    public void undoChanges() {
    }
}
