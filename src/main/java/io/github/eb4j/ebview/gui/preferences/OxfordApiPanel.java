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
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.FlowLayout;

public class OxfordApiPanel extends JPanel {
    /**
     * Creates a new panel to configure oxford dictionaries API's ID and Key.
     */
    public OxfordApiPanel() {
        initComponents();
    }

    private void initComponents() {
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        JLabel desc = new JLabel();
        desc.setText(LStrings.getString("PREFS_OXFORD_DESC"));
        add(desc);
        JPanel panel1 = new JPanel();
        panel1.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel appIdLabel = new JLabel();
        appIdLabel.setText(LStrings.getString("PREFS_OXFORD_APPID_LABEL"));
        panel1.add(appIdLabel);
        appIdTextField = new JTextField(10);
        panel1.add(appIdTextField);
        JPanel panel2 = new JPanel();
        panel2.setLayout(new FlowLayout(FlowLayout.LEFT));
        JLabel appKeyLabel = new JLabel();
        appKeyLabel.setText(LStrings.getString("PREFS_OXFORD_APPKEY_LABEL"));
        appKeyTextField = new JTextField(30);
        panel2.add(appKeyLabel);
        panel2.add(appKeyTextField);
        add(panel1);
        add(panel2);
    }

    JTextField appIdTextField;
    JTextField appKeyTextField;

}
