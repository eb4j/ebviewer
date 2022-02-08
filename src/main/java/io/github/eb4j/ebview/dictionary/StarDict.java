package io.github.eb4j.ebview.dictionary;

import io.github.eb4j.ebview.data.DictionaryEntry;
import io.github.eb4j.ebview.data.IDictionary;
import io.github.eb4j.stardict.StarDictDictionary;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

import java.io.File;
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
        protected final String dictionaryName;

        public StardictDict(final File file) throws Exception {
            dictionary = StarDictDictionary.loadDictionary(file);
            dictionaryName = dictionary.getDictionaryName();
        }

        @Override
        public String getDictionaryName() {
            return dictionaryName;
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
            return dictionary.readArticles(word).stream()
                    .filter(this::useEntry)
                    .map(this::getEntry)
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
            return dictionary.readArticlesPredictive(word).stream()
                    .filter(this::useEntry)
                    .map(this::getEntry)
                    .collect(Collectors.toList());
        }

        private DictionaryEntry getEntry(StarDictDictionary.Entry en) {
            if (en.getType() == StarDictDictionary.EntryType.HTML) {
                return new DictionaryEntry(en.getWord(), cleanHtmlArticle(en.getArticle()), dictionaryName);
            }
            return new DictionaryEntry(en.getWord(), en.getArticle(), dictionaryName);
        }

        private String cleanHtmlArticle(final String htmlText) {
            Safelist whitelist = new Safelist();
            whitelist.addTags("b", "br");
            whitelist.addAttributes("font", "color", "face");
            whitelist.addAttributes("a", "href");
            return Jsoup.clean(htmlText, whitelist);
        }

        private boolean useEntry(StarDictDictionary.Entry en) {
            StarDictDictionary.EntryType type = en.getType();
            return type == StarDictDictionary.EntryType.MEAN
                    || type == StarDictDictionary.EntryType.PHONETIC
                    || type == StarDictDictionary.EntryType.HTML;
        }
    }
}
