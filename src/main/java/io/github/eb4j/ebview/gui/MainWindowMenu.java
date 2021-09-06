package io.github.eb4j.ebview.gui;

import io.github.eb4j.ebview.gui.dialogs.AboutDialog;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class MainWindowMenu implements ActionListener, MenuListener {

    /** MainWindow instance. */
    protected final MainWindow mainWindow;

    public MainWindowMenu(final MainWindow mainWindow) {
        this.mainWindow = mainWindow;
    }

    /**
     * Invoked when an action occurs.
     *
     * @param e the event to be processed
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        String action = e.getActionCommand();
        invokeAction(action, e.getModifiers());
    }

    public void quitActionPerformed() {
        System.exit(0);
    }

    public void aboutActionPerformed() {
        new AboutDialog(mainWindow).setVisible(true);
    }

    public void invokeAction(String action, int modifiers) {
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
        Object[] args = method.getParameterTypes().length == 0 ? null : new Object[] { modifiers };
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
    public void menuSelected(MenuEvent e) {
    }

    /**
     * Invoked when the menu is deselected.
     *
     * @param e a MenuEvent object
     */
    @Override
    public void menuDeselected(MenuEvent e) {
    }

    /**
     * Invoked when the menu is canceled.
     *
     * @param e a MenuEvent object
     */
    @Override
    public void menuCanceled(MenuEvent e) {
    }

    JMenuBar initMenuComponents() {
        fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        fileMenu.addMenuListener(this);
        //
        fileQuitMenuItem = new JMenuItem("Quit");
        fileQuitMenuItem.setMnemonic(KeyEvent.VK_Q);
        fileQuitMenuItem.setActionCommand("quit");
        fileQuitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK));
        fileQuitMenuItem.addActionListener(this);
        //
        fileMenu.add(fileQuitMenuItem);
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
        mainMenu.add(fileMenu);
        mainMenu.add(helpMenu);
        return mainMenu;
    }

    JMenuBar mainMenu;
    JMenu fileMenu;
    JMenuItem fileQuitMenuItem;
    JMenu helpMenu;
    JMenuItem helpAboutMenuItem;
}
