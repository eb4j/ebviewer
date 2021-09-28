package io.github.eb4j.ebview.dictionary;

import io.github.eb4j.ebview.data.IDictionary;
import io.github.eb4j.ebview.dictionary.pdic.PdicDictionary;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Hiroshi Miura
 */
public class PDic implements IDictionaryFactory {
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
        return file.getPath().endsWith(".DIC") || file.getPath().endsWith(".dic");
    }

    /**
     * Load the given file and return an {@link IDictionary} that wraps it.
     *
     * @param file The file to load
     * @return An IDictionary file that can read articles from the file
     */
    @Override
    public Set<IDictionary> loadDict(final File file) {
        Set<IDictionary> result = new HashSet<>();
        try {
            IDictionary dictionary = new PdicDictionary(file);
            result.add(dictionary);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
