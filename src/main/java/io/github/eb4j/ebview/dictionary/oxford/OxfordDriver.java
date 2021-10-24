package io.github.eb4j.ebview.dictionary.oxford;

import io.github.eb4j.ebview.data.DictionaryEntry;
import io.github.eb4j.ebview.data.IDictionary;
import tokyo.northside.oxfordapi.OxfordClient;
import tokyo.northside.oxfordapi.OxfordClientException;
import tokyo.northside.oxfordapi.dtd.LexicalEntry;
import tokyo.northside.oxfordapi.dtd.Result;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OxfordDriver implements IDictionary {

    private final OxfordClient client;
    private final String source;
    private static final String APPID = "";  // FIXME
    private static final String APPKEY = "";  // FIXME: add GUI to set and store encrypted
    private final Map<String, List<DictionaryEntry>> cache = new HashMap<>();

    public OxfordDriver() {
        source = "en-gb";
        client = new OxfordClient(APPID, APPKEY);
    }

    @Override
    public String getDictionaryName() {
        return "Oxford Dictionaries";
    }

    /**
     * Read article's text.
     *
     * @param word The word to look up in the dictionary
     * @return List of entries. May be empty, but cannot be null.
     */
    @Override
    public List<DictionaryEntry> readArticles(final String word) {
        return queryArticle(word, true);
    }

    /**
     * Read article's text. Matching is predictive, so e.g. supplying "term"
     * will return articles for "term", "terminology", "termite", etc.
     *
     * @param word The word to look up in the dictionary
     * @return List of entries. May be empty, but cannot be null.
     */
    @Override
    public List<DictionaryEntry> readArticlesPredictive(final String word) {
        return queryArticle(word, false);
    }

    private List<DictionaryEntry> queryArticle(final String word, final boolean strict) {
        if (!cache.containsKey(word)) {
            List<DictionaryEntry> dictionaryEntries = new ArrayList<>();
            try {
                for (Result result: client.getEntries(word, source, strict)) {
                    for (LexicalEntry lexicalEntry : result.getLexicalEntries()) {
                        dictionaryEntries.add(HTMLFormatter.formatDefinitions(lexicalEntry));
                    }
                }
            } catch (OxfordClientException oce) {
                // when got connection/query error, return without any content.
                return Collections.emptyList();
            }
            cache.put(word, dictionaryEntries);
        }
        return cache.get(word);
    }


    @Override
    public void close() {
    }
}
