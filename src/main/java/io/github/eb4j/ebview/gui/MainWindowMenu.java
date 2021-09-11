package io.github.eb4j.ebview.gui;

import io.github.eb4j.ebview.dictionary.DictionariesManager;
import io.github.eb4j.ebview.gui.dialogs.AboutDialog;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
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

public class MainWindowMenu implements ActionListener, MenuListener, IMainMenu {

    /** MainWindow instance. */
    protected final IMainWindow mainWindow;

    public MainWindowMenu(final IMainWindow mainWindow) {
        this.mainWindow = mainWindow;
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
        if (JFileChooser.APPROVE_OPTION == chooser.showOpenDialog(mainWindow.getApplicationFrame())) {
            // do open dictionary
            DictionariesManager manager = mainWindow.getDictionariesManager();
            manager.loadDictionaries(chooser.getSelectedFile());
        }
    }

    public void quitActionPerformed() {
        JFrame app = mainWindow.getApplicationFrame();
        app.setVisible(false);
        app.dispose();
    }

    public void aboutActionPerformed() {
        new AboutDialog(mainWindow.getApplicationFrame()).setVisible(true);
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

    JMenuBar initMenuComponents() {
        fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        fileMenu.addMenuListener(this);
        //
        fileOpenMenuItem = new JMenuItem("Open");
        fileOpenMenuItem.setMnemonic(KeyEvent.VK_O);
        fileOpenMenuItem.setActionCommand("open");
        fileOpenMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
        fileOpenMenuItem.addActionListener(this);
        //
        fileQuitMenuItem = new JMenuItem("Quit");
        fileQuitMenuItem.setMnemonic(KeyEvent.VK_Q);
        fileQuitMenuItem.setActionCommand("quit");
        fileQuitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, InputEvent.CTRL_DOWN_MASK));
        fileQuitMenuItem.addActionListener(this);
        //
        fileMenu.add(fileOpenMenuItem);
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

    private JMenuBar mainMenu;
    private JMenu fileMenu;
    private JMenuItem fileOpenMenuItem;
    private JMenuItem fileQuitMenuItem;
    private JMenu helpMenu;
    private JMenuItem helpAboutMenuItem;
}
