package io.github.eb4j.ebview.gui;

import io.github.eb4j.ebview.dictionary.DictionariesManager;

import javax.swing.JFrame;
import java.awt.Font;

public interface IMainWindow {
    void showMessage(String msg);

    JFrame getApplicationFrame();

    Font getApplicationFont();
}
