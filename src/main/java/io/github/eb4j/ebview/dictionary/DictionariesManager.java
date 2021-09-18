package io.github.eb4j.ebview.dictionary;

import io.github.eb4j.ebview.data.DictionaryEntry;
import io.github.eb4j.ebview.data.IDictionary;
import io.github.eb4j.ebview.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    static final Logger LOG = LoggerFactory.getLogger(DictionariesManager.class.getName());

    protected final List<IDictionaryFactory> factories = new ArrayList<>();
    protected final List<IDictionary> dictionaries = new ArrayList<>();

    public DictionariesManager() {
        factories.add(new EPWING());
        factories.add(new LingvoDSL());
        factories.add(new StarDict());
    }

    public void closeDictionaries() {
        synchronized (this) {
            dictionaries.stream().forEach(this::closeDict);
            dictionaries.clear();
        }
    }

    public void closeDict(final IDictionary dict) {
        try {
            dict.close();
            LOG.info("-- remove " + dict.getDictionaryName());
        } catch (Exception e) {
            LOG.error("Dictionary error: ", e);
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
                for (IDictionary dict: dicts) {
                    LOG.info("-- add " + dict.getDictionaryName());
                }
                return;
            }
        }
    }

    public List<DictionaryEntry> findWord(final String word) {
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
