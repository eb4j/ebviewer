package io.github.eb4j.ebview.dictionary;

import io.github.eb4j.ebview.data.IDictionary;
import io.github.eb4j.ebview.dictionary.mdict.MDictDictionaryImpl;
import io.github.eb4j.mdict.MDException;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class MDict implements IDictionaryFactory {
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
        return file.getPath().endsWith(".MDX") || file.getPath().endsWith(".mdx");
    }

    /**
     * Load the given file and return an {@link IDictionary} that wraps it.
     *
     * @param file The file to load
     * @return An IDictionary file that can read articles from the file
     */
    @Override
    public Set<IDictionary> loadDict(final File file) throws MDException, IOException {
        Set<IDictionary> result = new HashSet<>();
        result.add(new MDictDictionaryImpl(file));
        return result;
    }
}
