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

import io.github.eb4j.ebview.utils.LStrings;
import io.github.eb4j.ebview.utils.Preferences;
import io.github.eb4j.ebview.utils.StaticUIUtils;

import javax.swing.JDialog;
import javax.swing.JTabbedPane;
import javax.swing.border.LineBorder;
import java.awt.Color;
import java.awt.Window;

public class PreferenceController {

    private IPreferencesController secureStoreController;
    private IPreferencesController oxfordApiController;

    public void show(final Window parent) {
        JDialog dialog = new JDialog(parent);
        dialog.setTitle(LStrings.getString("PASSWORD_DIALOG_TITLE"));
        dialog.setSize(450, 300);
        dialog.setModal(true);
        StaticUIUtils.setEscapeClosable(dialog);
        PreferencePanel preferencePanel = new PreferencePanel();

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBorder(new LineBorder(Color.DARK_GRAY));
        secureStoreController = new SecureStoreController();
        oxfordApiController = new OxfordApiController();
        tabbedPane.addTab("Oxford dictionaries", oxfordApiController.getGui());
        tabbedPane.addTab("Credentials", secureStoreController.getGui());
        preferencePanel.prefsPanel.add(tabbedPane);

        preferencePanel.okButton.addActionListener(e -> {
            secureStoreController.persist();
            oxfordApiController.persist();
            Preferences.save();
            StaticUIUtils.closeWindowByEvent(dialog);
        });
        preferencePanel.cancelButton.addActionListener(e -> StaticUIUtils.closeWindowByEvent(dialog));

        dialog.add(preferencePanel);
        dialog.pack();
        dialog.setLocationRelativeTo(parent);
        dialog.setVisible(true);
    }

}
