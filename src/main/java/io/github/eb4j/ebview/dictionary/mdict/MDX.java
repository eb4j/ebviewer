package io.github.eb4j.ebview.dictionary.mdict;

import io.github.eb4j.ebview.data.DictionaryEntry;
import io.github.eb4j.ebview.data.IDictionary;
import io.github.eb4j.mdict.MDException;
import io.github.eb4j.mdict.MDictDictionary;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MDX implements IDictionary {

    private MDictDictionary mdictionary;

    public MDX(final File mdxFile) throws MDException, IOException {
        String mdxPath = mdxFile.getPath();
        mdictionary = MDictDictionary.loadDicitonary(mdxPath);
    }

    @Override
    public String getDictionaryName() {
        return mdictionary.getTitle();
    }

    /**
     * Read article's text.
     *
     * @param word The word to look up in the dictionary
     * @return List of entries. May be empty, but cannot be null.
     */
    @Override
    public List<DictionaryEntry> readArticles(final String word) throws Exception {
        List<DictionaryEntry> result = new ArrayList<>();
        for (Map.Entry<String, Object> entry: mdictionary.getEntries(word)) {
            addEntry(result, entry);
        }
        return result;
    }

    /**
     * Read article's text. Matching is predictive, so e.g. supplying "term"
     * will return articles for "term", "terminology", "termite", etc. The
     * default implementation simply calls {@link #readArticles(String)} for
     * backwards compatibility.
     *
     * @param word The word to look up in the dictionary
     * @return List of entries. May be empty, but cannot be null.
     */
    @Override
    public List<DictionaryEntry> readArticlesPredictive(String word) throws Exception {
        List<DictionaryEntry> result = new ArrayList<>();
        for (Map.Entry<String, Object> entry: mdictionary.getEntriesPredictive(word)) {
            addEntry(result, entry);
        }
        return result;
    }

    private void addEntry(final List<DictionaryEntry> result, final Map.Entry<String, Object> entry) throws MDException {
        if (entry.getValue() instanceof Long) {
            result.add(new DictionaryEntry(entry.getKey(),
                    cleaHtmlArticle(mdictionary.getText((Long) entry.getValue())), getDictionaryName()));
        } else {
            Long[] values = (Long[]) entry.getValue();
            for (int i = 0; i < values.length; i++) {
                result.add(new DictionaryEntry(entry.getKey(), cleaHtmlArticle(mdictionary.getText(values[i])),
                        getDictionaryName()));
            }
        }
    }

    private String cleaHtmlArticle(final String mdictHtmlText) {
        Safelist whitelist = new Safelist();
        whitelist.addTags("b", "br");
        whitelist.addAttributes("font", "color", "face");
        return Jsoup.clean(mdictHtmlText, whitelist);
    }

}
