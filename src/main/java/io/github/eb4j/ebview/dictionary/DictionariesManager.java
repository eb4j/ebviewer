package io.github.eb4j.ebview.dictionary;

import io.github.eb4j.ebview.data.DictionaryEntry;
import io.github.eb4j.ebview.data.IDictionary;
import io.github.eb4j.ebview.utils.Stemmer;
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
 * @author Alex Buloichik
 * @author Didier Briel
 * @author Aaron Madlon-Kay
 * @author Hiroshi Miura
 */
public class DictionariesManager {

    static final Logger LOG = LoggerFactory.getLogger(DictionariesManager.class.getName());

    protected final List<IDictionaryFactory> factories = new ArrayList<>();
    protected final List<IDictionary> dictionaries = new ArrayList<>();
    private final Stemmer stemmer;

    public DictionariesManager() {
        factories.add(new EPWING());
        factories.add(new LingvoDSL());
        factories.add(new StarDict());
        factories.add(new PDic());
        factories.add(new MDict());
        stemmer = new Stemmer();
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
            LOG.info("remove " + dict.getDictionaryName());
        } catch (Exception e) {
            LOG.error("Dictionary error: ", e);
        }
    }

    public List<String> getDictionaryNames() {
        return dictionaries.stream().map(IDictionary::getDictionaryName).collect(Collectors.toUnmodifiableList());
    }

    /**
     * load dictionaries.
     * @param dictionaryDirectory directory where dictionary stored.
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
                    LOG.info("added " + dict.getDictionaryName());
                }
                return;
            }
        }
    }

    public List<DictionaryEntry> findWord(final String word) {
        List<DictionaryEntry> result;
        result = dictionaries.stream().flatMap(dict -> doLookUp(dict, word).stream()).collect(Collectors.toList());
        if (result.size() == 0) {
            String[] stemmed = stemmer.doStem(word);
            if (stemmed.length > 1) {
                result = dictionaries.stream()
                        .flatMap(dict -> doPredictiveLookup(dict, stemmed[0]).stream())
                        .collect(Collectors.toList());
            }
        }
        return result;
    }

    private List<DictionaryEntry> doPredictiveLookup(final IDictionary dict, final String word) {
        try {
            return dict.readArticlesPredictive(word);
        } catch (Exception ignored) {
            return Collections.emptyList();
        }
    }

    private List<DictionaryEntry> doLookUp(final IDictionary dict, final String word) {
        try {
            return dict.readArticles(word);
        } catch (Exception ignored) {
            return Collections.emptyList();
        }
    }
}
