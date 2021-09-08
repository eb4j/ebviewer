package io.github.eb4j.ebview.gui;

import io.github.eb4j.ebview.dictionary.DictionariesManager;

import javax.swing.JFrame;

public interface IMainWindow {

    DictionariesManager getDictionariesManager();

    JFrame getApplicationFrame();
}
