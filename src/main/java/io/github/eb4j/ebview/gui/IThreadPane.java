package io.github.eb4j.ebview.gui;

import io.github.eb4j.ebview.data.DictionaryEntry;

import java.util.List;

/**
 * Interface to update contents from thread.
 */
public interface IThreadPane {

    void setFoundResult(List<DictionaryEntry> data);

}
