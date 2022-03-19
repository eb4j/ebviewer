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

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.border.TitledBorder;
import java.awt.GridBagConstraints;

public class AppearancePanel extends JPanel {

    public AppearancePanel() {
        initComponents();
    }

    private void initComponents() {
        GridBagConstraints gridBagConstraints;
        JLabel desc = new JLabel();
        desc.setText(LStrings.getString("PREFS_APPEARANCE_DESC"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(desc, gridBagConstraints);

        condensedModeCB = new JCheckBox();
        condensedModeCB.setText(LStrings.getString("PREFS_APPEARANCE_CONDENSED_MODE"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(condensedModeCB, gridBagConstraints);

        fontComboBox = new javax.swing.JComboBox<>();
        JLabel fontLabel = new JLabel();
        sizeSpinner = new javax.swing.JSpinner();
        JLabel sizeLabel = new JLabel();
        previewTextArea = new javax.swing.JTextArea();

        setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setLayout(new java.awt.GridBagLayout());

        fontComboBox.setMaximumRowCount(20);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(fontComboBox, gridBagConstraints);

        fontLabel.setText(LStrings.getString("PREFS_SELECT_FONT"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(fontLabel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(sizeSpinner, gridBagConstraints);
        sizeLabel.setText(LStrings.getString("PREFS_SELECT_FONTSIZE"));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(sizeLabel, gridBagConstraints);

        previewTextArea.setEditable(false);
        previewTextArea.setLineWrap(true);
        previewTextArea.setText(LStrings.getString("PREFS_FONT_SAMPLE_TEXT")); // NOI18N
        previewTextArea.setWrapStyleWord(true);
        previewTextArea.setBorder(BorderFactory.createTitledBorder(
                null,
                LStrings.getString("PREFS_FONT_SAMPLE_TITLE"),
                TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION,
                fontLabel.getFont()));
        previewTextArea.setOpaque(false);
        previewTextArea.setPreferredSize(new java.awt.Dimension(116, 100));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(previewTextArea, gridBagConstraints);

    }

    JCheckBox condensedModeCB;
    JComboBox<String> fontComboBox;
    JTextArea previewTextArea;
    JSpinner sizeSpinner;
}
