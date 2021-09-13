package io.github.eb4j.ebview.dictionary;

import io.github.eb4j.ebview.data.DictionaryEntry;
import io.github.eb4j.ebview.data.IDictionary;
import io.github.eb4j.ebview.utils.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
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
    protected final List<IDictionary> dictionaries = new ArrayList<>();

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

    /**
     * load dictionaries.
     * @param dictionaryDirectory directory where dictinary stored.
     */
    public void loadDictionaries(final File dictionaryDirectory) {
        List<File> listFiles = FileUtils.findFiles(dictionaryDirectory);
        for (File f : listFiles) {
            try {
                loadDictionary(f);
            } catch (Exception ignore) {
            }
        }
    }

    private void loadDictionary(final File file) throws Exception {
        if (!file.isFile()) {
            return;
        }
        for (IDictionaryFactory factory: factories) {
            if (factory.isSupportedFile(file)) {
                Set<IDictionary> dicts = factory.loadDict(file);
                dictionaries.addAll(dicts);
                System.err.println("-- add " + file.getPath());
                return;
            }
        }
    }

    public List<DictionaryEntry> findWord(final String word) {
        List<IDictionary> dicts;
        return dictionaries.stream().flatMap(dict -> doLookUp(dict, word).stream()).collect(Collectors.toList());
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
