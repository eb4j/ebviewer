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


import io.github.eb4j.ebview.utils.LStrings;

import javax.swing.JPanel;

/**
 * @author Aaron Madlon-Kay
 */
@SuppressWarnings("serial")
public class SecureStorePanel extends JPanel {

    /** Creates new form SecureStorePanel */
    public SecureStorePanel() {
        initComponents();
    }

    private void initComponents() {

        descriptionTextArea = new javax.swing.JTextArea();
        jPanel1 = new JPanel();
        masterPasswordLabel = new javax.swing.JLabel();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 0), new java.awt.Dimension(5, 32767));
        masterPasswordStatusLabel = new javax.swing.JLabel();
        resetPasswordButton = new javax.swing.JButton();

        setBorder(javax.swing.BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setMinimumSize(new java.awt.Dimension(250, 200));
        setLayout(new javax.swing.BoxLayout(this, javax.swing.BoxLayout.PAGE_AXIS));

        descriptionTextArea.setEditable(false);
        descriptionTextArea.setFont(masterPasswordLabel.getFont());
        descriptionTextArea.setLineWrap(true);
        descriptionTextArea.setText(LStrings.getString("PREFS_SECURE_STORE_DESCRIPTION")); // NOI18N
        descriptionTextArea.setWrapStyleWord(true);
        descriptionTextArea.setAlignmentX(0.0F);
        descriptionTextArea.setDragEnabled(false);
        descriptionTextArea.setFocusable(false);
        descriptionTextArea.setOpaque(false);
        add(descriptionTextArea);

        jPanel1.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 0, 5, 0));
        jPanel1.setAlignmentX(0.0F);
        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1, javax.swing.BoxLayout.LINE_AXIS));

        masterPasswordLabel.setLabelFor(masterPasswordStatusLabel);
        masterPasswordLabel.setText(LStrings.getString("PREFS_SECURE_STORE_MASTER_PASSWORD_LABEL"));
        jPanel1.add(masterPasswordLabel);
        jPanel1.add(filler1);

        masterPasswordStatusLabel.setFont(masterPasswordStatusLabel.getFont().deriveFont(masterPasswordStatusLabel.getFont().getStyle() | java.awt.Font.BOLD));
        jPanel1.add(masterPasswordStatusLabel);

        add(jPanel1);

        resetPasswordButton.setText(LStrings.getString("PREFS_SECURE_STORAGE_RESET_BUTTON"));
        add(resetPasswordButton);
    }

    public javax.swing.JTextArea descriptionTextArea;
    private javax.swing.Box.Filler filler1;
    private JPanel jPanel1;
    private javax.swing.JLabel masterPasswordLabel;
    javax.swing.JLabel masterPasswordStatusLabel;
    javax.swing.JButton resetPasswordButton;
}
