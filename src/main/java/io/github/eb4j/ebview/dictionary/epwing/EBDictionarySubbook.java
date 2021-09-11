package io.github.eb4j.ebview.dictionary.epwing;

import io.github.eb4j.EBException;
import io.github.eb4j.Result;
import io.github.eb4j.Searcher;
import io.github.eb4j.SubBook;
import io.github.eb4j.ebview.data.DictionaryEntry;
import io.github.eb4j.ebview.data.IDictionary;
import io.github.eb4j.hook.Hook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class EBDictionarySubbook implements IDictionary {

    private final SubBook subBook;

    static final Logger LOG = LoggerFactory.getLogger(EBDictionarySubbook.class.getName());

    public enum Mode {
        PREDICTIVE,
        EXACT,
    }

    public EBDictionarySubbook(SubBook subBook) {
        this.subBook = subBook;
    }

    private static void logEBError(final EBException e) {
        switch (e.getErrorCode()) {
            case EBException.CANT_READ_DIR:
                LOG.warn("EPWING error: cannot read directory:" + e.getMessage());
                break;
            case EBException.DIR_NOT_FOUND:
                LOG.warn("EPWING error: cannot found directory:" + e.getMessage());
            default:
                LOG.warn("EPWING error: " + e.getMessage());
                break;
        }
    }

    /**
     * Predictive search.
     * @param word
     *            The word to look up in the dictionary
     * @return article string.
     */
    public List<DictionaryEntry> readArticlesPredictive(final String word) {
        return readArticles(word, Mode.PREDICTIVE);
    }

    @Override
    public String getDictionaryName() {
        return subBook.getTitle();
    }

    /*
     * Returns not the raw text, but the formatted article ready for
     * upstream use (\n replaced with <br>, etc.
     */
    public List<DictionaryEntry> readArticles(final String word) {
        return readArticles(word, Mode.EXACT);
    }

    private List<DictionaryEntry> readArticles(final String word, final Mode mode) {
        Searcher sh;
        Result searchResult;
        Hook<String> hook;
        String article;
        String heading;
        Set<String> headings = new HashSet<>();
        List<DictionaryEntry> result = new ArrayList<>();
        String path = subBook.getName();
        try {
            hook = new EBDictStringHook(subBook);
            if (mode.equals(Mode.PREDICTIVE) && subBook.hasWordSearch()) {
                sh = subBook.searchWord(word);
            } else {
                sh = subBook.searchExactword(word);
            }
            while ((searchResult = sh.getNextResult()) != null) {
                heading = searchResult.getHeading(hook);
                if (headings.contains(heading)) {
                    continue;
                }
                headings.add(heading);
                article = searchResult.getText(hook);
                result.add(new DictionaryEntry(heading, article, getDictionaryName()));
            }
        } catch (EBException e) {
            logEBError(e);
        }
        return result;
    }

    /**
     * Dispose IDictionary. Default is no action.
     */
    @Override
    public void close() {
    }
}
