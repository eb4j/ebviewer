package io.github.eb4j.ebview.dictionary;

import io.github.eb4j.ebview.data.DictionaryEntry;
import io.github.eb4j.ebview.data.IDictionary;
import io.github.eb4j.stardict.StarDictDictionary;
import io.github.eb4j.stardict.StarDictLoader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Dictionary driver for StarDict format.
 * @author Hiroshi Miura
 */
public class StarDict implements IDictionaryFactory {

    @Override
    public boolean isSupportedFile(final File file) {
        return file.getPath().endsWith(".ifo");
    }

    @Override
    public Set<IDictionary> loadDict(final File ifoFile) throws Exception {
        return Collections.singleton(new StardictDict(ifoFile));
    }

    /**
     * Dictionary implementation for stardict.
     *
     * @author Hiroshi Miura
     */
    public static class StardictDict implements IDictionary {

        protected final StarDictDictionary dictionary;

        public StardictDict(final File file) throws Exception {
            dictionary = StarDictDictionary.loadDictionary(file);
        }

        @Override
        public String getDictionaryName() {
            return dictionary.getDictionaryName();
        }

        /**
         * read article with exact match.
         * @param word
         *            The word to look up in the dictionary
         *
         * @return list of results.
         */
        @Override
        public List<DictionaryEntry> readArticles(final String word) {
            List<DictionaryEntry> result = new ArrayList<>();
            String dictionaryName = getDictionaryName();
            return dictionary.readArticles(word).stream()
                    .filter(en -> useEntry(en.getType()))
                    .map(en -> new DictionaryEntry(en.getWord(), en.getArticle(), dictionaryName))
                    .collect(Collectors.toList());
        }

        /**
         * read article with predictive match.
         * @param word
         *            The word to look up in the dictionary
         *
         * @return list of results.
         */
        @Override
        public List<DictionaryEntry> readArticlesPredictive(final String word) {
            List<DictionaryEntry> result = new ArrayList<>();
            String dictionaryName = getDictionaryName();
            return dictionary.readArticlesPredictive(word).stream()
                    .filter(en -> useEntry(en.getType()))
                    .map(en -> new DictionaryEntry(en.getWord(), en.getArticle(), dictionaryName))
                    .collect(Collectors.toList());
        }

        private static boolean useEntry(StarDictDictionary.EntryType type) {
            return type == StarDictDictionary.EntryType.MEAN
                    || type == StarDictDictionary.EntryType.PHONETIC
                    || type == StarDictDictionary.EntryType.HTML;
        }
    }
}
