package io.github.eb4j.ebview.dictionary.oxford;

import io.github.eb4j.ebview.data.DictionaryEntry;
import io.github.eb4j.ebview.data.IDictionary;
import io.github.eb4j.ebview.utils.CredentialsManager;
import org.apache.commons.lang3.StringUtils;
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

    public static final String PROPERTY_API_ID = "oxford.api.id";
    public static final String PROPERTY_API_KEY = "oxford.api.key";

    private final String source;
    private final Map<String, List<DictionaryEntry>> cache = new HashMap<>();

    public OxfordDriver() {
        source = "en-gb";
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
        String appId = CredentialsManager.getCredential(PROPERTY_API_ID);
        String appKey = CredentialsManager.getCredential(PROPERTY_API_KEY);
        if (StringUtils.isEmpty(appId) || StringUtils.isEmpty(appKey)) {
            return Collections.emptyList();
        }
        OxfordClient client = new OxfordClient("id", "key");
        if (!cache.containsKey(word)) {
            List<DictionaryEntry> dictionaryEntries = new ArrayList<>();
            try {
                for (Result result: client.queryEntry(word, source, strict)) {
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
