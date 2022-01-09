package io.github.eb4j.ebview.dictionary.pdic;

import io.github.eb4j.ebview.data.DictionaryEntry;
import io.github.eb4j.ebview.data.IDictionary;
import io.github.eb4j.pdic.PdicDictionary;
import io.github.eb4j.pdic.PdicElement;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * @author wak (Apache-2.0)
 * @author Hiroshi Miura
 */
public class PdicDict implements IDictionary {

    private final PdicDictionary dict;
    private final Locale sourceLocale;
    private final String dictionaryName;

    /**
     * Construct with .dic file.
     * It create index cache file with name .dic.idx.
     *
     * @param file PDIC .dic file.
     * @throws IOException when access error occurred.
     */
    public PdicDict(final File file) throws IOException {
        File cache = new File(file.getPath() + ".idx");
        sourceLocale = Locale.ROOT;
        this.dict = PdicDictionary.loadDictionary(file, cache);
        dictionaryName = file.getName();
    }

    @Override
    public String getDictionaryName() {
        return dictionaryName;
    }

    /**
     * Read article's text.
     *
     * @param word The word to look up in the dictionary
     * @return List of entries. May be empty, but cannot be null.
     */
    @Override
    public List<DictionaryEntry> readArticles(final String word) throws IOException {
        return makeDictionaryEntries(dict.getEntries(word.toLowerCase(sourceLocale)));
    }

    /**
     * Read article's text. Matching is predictive, so e.g. supplying "term"
     * will return articles for "term", "terminology", "termite", etc.
     *
     * @param word The word to look up in the dictionary
     * @return List of entries. May be empty, but cannot be null.
     */
    @Override
    public List<DictionaryEntry> readArticlesPredictive(final String word) throws IOException {
        return makeDictionaryEntries(dict.getEntriesPredictive(word.toLowerCase(sourceLocale)));
    }

    /**
     * Dispose IDictionary. Default is no action.
     */
    @Override
    public void close() {
    }

    private List<DictionaryEntry> makeDictionaryEntries(final List<PdicElement> results) {
        List<DictionaryEntry> lists = new ArrayList<>();
        for (PdicElement result : results) {
            String word = result.getHeadWord();
            if (word.equals("")) {
                word = result.getIndexWord();
            }
            StringBuilder articleBuilder = new StringBuilder();
            String pronunciation = result.getPronunciation();
            if (pronunciation != null) {
                articleBuilder.append(pronunciation).append(" / ");
            }
            articleBuilder.append(result.getTranslation()).append("<br/>");
            String example = result.getExample();
            if (example != null) {
                articleBuilder.append(example);
            }
            lists.add(new DictionaryEntry(word, articleBuilder.toString(), dictionaryName));
        }
        return lists;
    }

}
