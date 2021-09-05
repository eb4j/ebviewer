package io.github.eb4j.ebview.dictionary.stardict;

import io.github.eb4j.ebview.data.DictionaryData;
import io.github.eb4j.ebview.data.DictionaryEntry;
import io.github.eb4j.ebview.data.IDictionary;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

abstract class StarDictBaseDict implements IDictionary {

    protected final DictionaryData<StarDictEntry> data;

    private final Map<StarDictEntry, String> cache = new HashMap<>();

    /**
     * @param data collection of <code>Entry</code>s loaded from file
     */
    StarDictBaseDict(DictionaryData<StarDictEntry> data) {
        this.data = data;
    }

    @Override
    public List<DictionaryEntry> readArticles(String word) throws Exception {
        return data.lookUp(word).stream()
                .map(e -> new DictionaryEntry(e.getKey(), getArticle(e.getValue()), getDictionaryName()))
                .collect(Collectors.toList());
    }

    @Override
    public List<DictionaryEntry> readArticlesPredictive(String word) {
        return data.lookUpPredictive(word).stream()
                .map(e -> new DictionaryEntry(e.getKey(), getArticle(e.getValue()), getDictionaryName()))
                .collect(Collectors.toList());
    }

    private synchronized String getArticle(StarDictEntry starDictEntry) {
        return cache.computeIfAbsent(starDictEntry, (e) -> {
            return readArticle(e.getStart(), e.getLen()).replace("\n", "<br>");
        });
    }

    protected abstract String getDictionaryName();

    /**
     * Read data from the underlying file.
     *
     * @param start Start offset in data file
     * @param len   Length of article data
     * @return Raw article text
     */
    protected abstract String readArticle(int start, int len);
}
