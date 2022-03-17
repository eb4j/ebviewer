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

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;

public class PreferencePanel extends JPanel {

    public PreferencePanel() {
        initComponents();
    }

    private void initComponents() {
        okButton = new JButton();
        cancelButton = new JButton();
        bottomPanel = new JPanel();
        prefsPanel = new JPanel();

        setMinimumSize(new java.awt.Dimension(250, 200));
        setLayout(new BorderLayout());
        add(prefsPanel, BorderLayout.CENTER);

        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));
        bottomPanel.setMinimumSize(new Dimension(400, 100));
        okButton.setText(LStrings.getString("BUTTON_OK"));
        cancelButton.setText(LStrings.getString("BUTTON_CANCEL"));
        bottomPanel.add(okButton);
        bottomPanel.add(cancelButton);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    JPanel prefsPanel;
    JPanel bottomPanel;
    JButton okButton;
    JButton cancelButton;
}
