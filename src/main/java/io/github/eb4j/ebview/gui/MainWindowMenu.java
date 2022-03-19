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

package io.github.eb4j.ebview.gui;

import io.github.eb4j.ebview.EBViewer;
import io.github.eb4j.ebview.core.Core;
import io.github.eb4j.ebview.dictionary.DictionariesManager;
import io.github.eb4j.ebview.gui.dialogs.AboutDialog;
import io.github.eb4j.ebview.gui.preferences.PreferenceController;
import io.github.eb4j.ebview.utils.LStrings;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import java.awt.AWTException;
import java.awt.Frame;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static io.github.eb4j.ebview.utils.ResourceUtil.APP_ICON_32X32;

public class MainWindowMenu implements ActionListener, MenuListener {

    private final JFrame app;
    private final DictionariesManager manager;
    private final TrayIcon trayIcon;

    public MainWindowMenu() {
        app = Core.getApplicationFrame();
        manager = Core.getDictionariesManager();
        trayIcon = new TrayIcon(APP_ICON_32X32, "EBViewer", null);
        initMenuComponents();
        initTray();
    }

    private void initTray() {
        trayIcon.setImageAutoSize(true);
        try {
            SystemTray.getSystemTray().add(trayIcon);
        } catch (AWTException ignore) {
        }
        if (SystemTray.isSupported()) {
            trayIcon.addActionListener(actionEvent -> {
                app.setVisible(true);
                app.setState(Frame.NORMAL);
            });
        }
    }

    /**
     * Invoked when an action occurs.
     *
     * @param e the event to be processed
     */
    @Override
    public void actionPerformed(final ActionEvent e) {
        String action = e.getActionCommand();
        invokeAction(action, e.getModifiers());
    }

    public void openActionPerformed() {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setDialogTitle("Open dictionary folder");
        if (JFileChooser.APPROVE_OPTION == chooser.showOpenDialog(app)) {
            manager.loadDictionaries(chooser.getSelectedFile());
        }
        StringBuilder sb = new StringBuilder("<h2>Current dictionaries loaded:</h2><ul>");
        for (String dict: manager.getDictionaryNames()) {
            sb.append("<li>").append(dict).append("</li>");
        }
        sb.append("</li>");
        Core.getMainWindow().showMessage(sb.toString());
    }

    public void closeActionPerformed() {
        manager.closeDictionaries();
    }

    public void trayActionPerformed() {
        app.setVisible(false);
    }

    public void quitActionPerformed() {
        SystemTray.getSystemTray().remove(trayIcon);
        app.setVisible(false);
        app.dispose();
    }

    public void aboutActionPerformed() {
        new AboutDialog(app).setVisible(true);
    }

    public void prefActionPerformed() {
        PreferenceController controller = new PreferenceController();
        try {
            controller.show(app);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @SuppressWarnings("avoidinlineconditionals")
    public void invokeAction(final String action, final int modifiers) {
        String methodName = action + "ActionPerformed";
        // find method
        Method method;
        try {
            method = this.getClass().getMethod(methodName);
        } catch (NoSuchMethodException ignore) {
            try {
                method = this.getClass().getMethod(methodName, Integer.TYPE);
            } catch (NoSuchMethodException ex) {
                throw new IncompatibleClassChangeError("Error invoke method handler for main menu");
            }
        }
        // call
        Object[] args = method.getParameterTypes().length == 0 ? null : new Object[] {modifiers};
        try {
            method.invoke(this, args);
        } catch (IllegalAccessException | InvocationTargetException ex) {
            throw new IncompatibleClassChangeError("Error invoke method handler for main menu");
        }
    }

    /**
     * Invoked when a menu is selected.
     *
     * @param e a MenuEvent object
     */
    @Override
    public void menuSelected(final MenuEvent e) {
    }

    /**
     * Invoked when the menu is deselected.
     *
     * @param e a MenuEvent object
     */
    @Override
    public void menuDeselected(final MenuEvent e) {
    }

    /**
     * Invoked when the menu is canceled.
     *
     * @param e a MenuEvent object
     */
    @Override
    public void menuCanceled(final MenuEvent e) {
    }

    public void initMenuComponents() {
        appMenu = new JMenu(LStrings.getString("MENU_APP"));
        appMenu.setMnemonic(KeyEvent.VK_W);
        appMenu.addMenuListener(this);
        //
        appPreferenceMenuItem = new JMenuItem(LStrings.getString("MENU_APP_PREFERENCE"));
        appPreferenceMenuItem.setMnemonic(KeyEvent.VK_P);
        appPreferenceMenuItem.setActionCommand("pref");
        appPreferenceMenuItem.addActionListener(this);
        //
        appTrayMenuItem = new JMenuItem(LStrings.getString("MENU_APP_QUIT_TRAY"));
        appTrayMenuItem.setMnemonic(KeyEvent.VK_Q);
        appTrayMenuItem.setActionCommand("tray");
        appTrayMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK));
        appTrayMenuItem.addActionListener(this);
        //
        appQuitMenuItem = new JMenuItem(LStrings.getString("MENU_APP_EXIT"));
        appQuitMenuItem.setMnemonic(KeyEvent.VK_E);
        appQuitMenuItem.setActionCommand("quit");
        appQuitMenuItem.addActionListener(this);
        //
        appMenu.add(appPreferenceMenuItem);
        appMenu.add(appTrayMenuItem);
        appMenu.add(appQuitMenuItem);
        //
        dictMenu = new JMenu("Dictionary");
        dictMenu.setMnemonic(KeyEvent.VK_D);
        dictMenu.addMenuListener(this);
        //
        dictOpenMenuItem = new JMenuItem("Add dictionaries");
        dictOpenMenuItem.setMnemonic(KeyEvent.VK_A);
        dictOpenMenuItem.setActionCommand("open");
        dictOpenMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK));
        dictOpenMenuItem.addActionListener(this);
        //
        dictCloseMenuItem = new JMenuItem("Close dictionaries");
        dictCloseMenuItem.setMnemonic(KeyEvent.VK_C);
        dictCloseMenuItem.setActionCommand("close");
        dictCloseMenuItem.addActionListener(this);
        //
        dictMenu.add(dictOpenMenuItem);
        dictMenu.add(dictCloseMenuItem);
        //
        helpMenu = new JMenu("Help");
        helpMenu.setMnemonic(KeyEvent.VK_H);
        helpMenu.addMenuListener(this);
        //
        helpAboutMenuItem = new JMenuItem("About");
        helpAboutMenuItem.setMnemonic(KeyEvent.VK_A);
        helpAboutMenuItem.setActionCommand("about");
        helpAboutMenuItem.addActionListener(this);
        //
        helpMenu.add(helpAboutMenuItem);
        //
        mainMenu = new JMenuBar();
        mainMenu.add(appMenu);
        mainMenu.add(dictMenu);
        mainMenu.add(helpMenu);
        app.setJMenuBar(mainMenu);
    }

    private JMenuBar mainMenu;
    private JMenu appMenu;
    private JMenuItem appPreferenceMenuItem;
    private JMenuItem appTrayMenuItem;
    private JMenuItem appQuitMenuItem;
    private JMenu dictMenu;
    private JMenuItem dictOpenMenuItem;
    private JMenuItem dictCloseMenuItem;
    private JMenu helpMenu;
    private JMenuItem helpAboutMenuItem;
}
