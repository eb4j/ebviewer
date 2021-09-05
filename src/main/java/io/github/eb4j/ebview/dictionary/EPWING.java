package io.github.eb4j.ebview.dictionary;

import io.github.eb4j.ebview.data.IDictionary;
import io.github.eb4j.ebview.dictionary.epwing.EBDictionary;

import java.io.File;

/**
 * Driver for EPWING.
 * @author Hiroshi Miura
 */
public class EPWING implements IDictionaryFactory {

    /**
     * Determine whether or not the supplied file is supported by this factory.
     * This is intended to be a lightweight check, e.g. looking for a file
     * extension.
     *
     * @param file The file to check
     * @return Whether or not the file is supported
     */
    @Override
    public boolean isSupportedFile(final File file) {
        return file.getPath().toUpperCase().endsWith("CATALOGS");
    }

    /**
     * Load the given file and return an {@link IDictionary} that wraps it.
     *
     * @param file The file to load
     * @return An IDictionary file that can read articles from the file
     * @throws Exception If the file could not be loaded for reasons that were not
     *                   determined by {@link #isSupportedFile(File)}
     */
    @Override
    public IDictionary loadDict(final File file) throws Exception {
        return new EBDictionary(file);
    }
}
