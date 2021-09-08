package io.github.eb4j.ebview.dictionary;

import io.github.eb4j.ebview.data.DictionaryEntry;
import io.github.eb4j.ebview.data.IDictionary;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;


/**
 * Class for load dictionaries.
 *
 * @author Alex Buloichik (alex73mail@gmail.com)
 * @author Didier Briel
 * @author Aaron Madlon-Kay
 */
public class DictionariesManager {

    protected final List<IDictionaryFactory> factories = new ArrayList<>();
    protected final Map<String, IDictionary> dictionaries = new TreeMap<>();

    public DictionariesManager() {
        factories.add(new EPWING());
        factories.add(new LingvoDSL());
        factories.add(new StarDict());
    }

    public void closeDict(final IDictionary dict) {
        try {
            dict.close();
        } catch (Exception e) {
            // Log.log(e);
        }
    }

    public boolean loadDictionary(final File file) throws Exception {
        if (!file.isFile()) {
            return false;
        }
        for (IDictionaryFactory factory: factories) {
            if (factory.isSupportedFile(file)) {
                IDictionary dict = factory.loadDict(file);
                dictionaries.put(file.getPath(), dict);
                System.err.println("-- add " + file.getPath());
                return true;
            }
        }
        return false;
    }

    public List<DictionaryEntry> findWord(final String word) {
        List<IDictionary> dicts;
        dicts = new ArrayList<>(dictionaries.values());
        return dicts.stream().flatMap(dict -> doLookUp(dict, word).stream()).collect(Collectors.toList());
    }

    private List<DictionaryEntry> doLookUp(final IDictionary dict, final String word) {
        try {
            List<DictionaryEntry> result = dict.readArticles(word);
            if (!result.isEmpty()) {
                return result;
            }
            return dict.readArticlesPredictive(word);
        } catch (Exception ex) {
            // Log.log(ex);
        }
        return Collections.emptyList();
    }

}
