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

import io.github.eb4j.ebview.utils.Preferences;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComponent;
import java.awt.Font;
import java.awt.GraphicsEnvironment;

public class AppearanceController implements  IPreferencesController {

    private AppearancePanel panel;

    public AppearanceController() {
    }

    public JComponent getGui() {
        if (panel == null) {
            panel = new AppearancePanel();
            panel.fontComboBox.setModel(new DefaultComboBoxModel<>(getFontNames()));
            panel.fontComboBox.addActionListener(e -> panel.previewTextArea.setFont(getSelectedFont()));
            panel.sizeSpinner.addChangeListener(e -> panel.previewTextArea.setFont(getSelectedFont()));
            initFromPrefs();
        }
        return panel;
    }

    private void initFromPrefs() {
        panel.condensedModeCB.setSelected(Preferences.isPreferenceDefault(Preferences.APPEARANCE_CONDENSED_VIEW, false));
        String fontName = Preferences.getPreferenceDefault(Preferences.APPEARANCE_FONT_NAME, Preferences.APPEARANCE_FONT_DEFAULT);
        int fontSize = Preferences.getPreferenceDefault(Preferences.APPEARANCE_FONT_SIZE, Preferences.APPEARANCE_FONT_SIZE_DEFAULT);
        Font oldFont = new Font(fontName, Font.PLAIN, fontSize);
        panel.previewTextArea.setFont(oldFont);
        panel.fontComboBox.setSelectedItem(oldFont.getName());
        panel.sizeSpinner.setValue(oldFont.getSize());
    }

    public void persist() {
        Font newFont = getSelectedFont();
        Preferences.setPreference(Preferences.APPEARANCE_CONDENSED_VIEW, panel.condensedModeCB.isSelected());
        Preferences.setPreference(Preferences.APPEARANCE_FONT_NAME, newFont.getName());
        Preferences.setPreference(Preferences.APPEARANCE_FONT_SIZE, newFont.getSize());
    }

    /**
     * Restore preferences controlled by this view to their current persisted
     * state.
     */
    @Override
    public void undoChanges() {
        initFromPrefs();
    }

    public void restoreDefaults() {
        panel.condensedModeCB.setSelected(false);
    }

     private Font getSelectedFont() {
        return new Font((String) panel.fontComboBox.getSelectedItem(), Font.PLAIN,
                ((Number) panel.sizeSpinner.getValue()).intValue());
    }

    private static String[] getFontNames() {
        GraphicsEnvironment graphics;
        graphics = GraphicsEnvironment.getLocalGraphicsEnvironment();
        return graphics.getAvailableFontFamilyNames();
    }
}
