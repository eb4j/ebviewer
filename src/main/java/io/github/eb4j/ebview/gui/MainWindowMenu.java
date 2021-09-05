package io.github.eb4j.ebview.gui;

import javax.swing.*;
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
        fileQuitMenu = new JMenuItem("Quit");
        fileQuitMenu.setMnemonic(KeyEvent.VK_Q);
        fileQuitMenu.setActionCommand("quit");
        fileQuitMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK));
        fileQuitMenu.addActionListener(this);
        //
        fileMenu.add(fileQuitMenu);
        //
        helpMenu = new JMenu("Help");
        //
        optionsMenu = new JMenu("Options");
        //
        mainMenu = new JMenuBar();
        mainMenu.add(fileMenu);
        // mainMenu.add(optionsMenu);
        // mainMenu.add(helpMenu);
        return mainMenu;
    }

    JMenuBar mainMenu;
    JMenu fileMenu;
    JMenuItem fileQuitMenu;
    JMenu helpMenu;
    JMenu optionsMenu;
}
