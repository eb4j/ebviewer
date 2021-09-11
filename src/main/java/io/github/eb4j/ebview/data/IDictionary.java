package io.github.eb4j.ebview.data;

import java.io.IOException;
import java.util.List;

/**
 * Interface for dictionary drivers.
 */
public interface IDictionary extends AutoCloseable {

    String getDictionaryName();

    /**
     * Read article's text.
     *
     * @param word
     *            The word to look up in the dictionary
     *
     * @return List of entries. May be empty, but cannot be null.
     */
    List<DictionaryEntry> readArticles(String word) throws Exception;

    /**
     * Read article's text. Matching is predictive, so e.g. supplying "term"
     * will return articles for "term", "terminology", "termite", etc. The
     * default implementation simply calls {@link #readArticles(String)} for
     * backwards compatibility.
     *
     * @param word
     *            The word to look up in the dictionary
     *
     * @return List of entries. May be empty, but cannot be null.
     */
    default List<DictionaryEntry> readArticlesPredictive(String word) throws Exception {
        // Default implementation for backwards compatibility
        return readArticles(word);
    }

    /**
     * Dispose IDictionary. Default is no action.
     */
    default void close() throws IOException { }
}
