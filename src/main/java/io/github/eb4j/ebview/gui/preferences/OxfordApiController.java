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

package io.github.eb4j.ebview.gui.preferences;

import io.github.eb4j.ebview.dictionary.oxford.OxfordDriver;
import io.github.eb4j.ebview.utils.CredentialsManager;
import org.apache.commons.lang3.StringUtils;

import javax.swing.JComponent;

public class OxfordApiController implements IPreferencesController {

    private OxfordApiPanel panel;

    public JComponent getGui() {
        if (panel == null) {
            panel = new OxfordApiPanel();
            initFromPrefs();
        }
        return panel;
    }

    protected void initFromPrefs() {
        CredentialsManager credentialsManager = CredentialsManager.getInstance();
        panel.appIdTextField.setText(credentialsManager.retrieve(OxfordDriver.PROPERTY_API_ID).orElse(""));
        panel.appKeyTextField.setText(credentialsManager.retrieve(OxfordDriver.PROPERTY_API_KEY).orElse(""));
    }

    public void persist() {
        String appId = panel.appIdTextField.getText();
        if (!StringUtils.isEmpty(appId)) {
            CredentialsManager credentialsManager = CredentialsManager.getInstance();
            credentialsManager.store(OxfordDriver.PROPERTY_API_ID, appId);
            credentialsManager.store(OxfordDriver.PROPERTY_API_KEY, panel.appKeyTextField.getText());
        }
    }

    /**
     * Restore preferences controlled by this view to their current persisted
     * state.
     */
    @Override
    public void undoChanges() {
    }

    /**
     * Restore preferences controlled by this view to their default state.
     */
    @Override
    public void restoreDefaults() {
        panel.appIdTextField.setText("");
        panel.appKeyTextField.setText("");
    }
}
