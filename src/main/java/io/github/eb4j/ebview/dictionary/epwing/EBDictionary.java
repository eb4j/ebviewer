package io.github.eb4j.ebview.dictionary.epwing;

import io.github.eb4j.Book;
import io.github.eb4j.EBException;
import io.github.eb4j.Result;
import io.github.eb4j.Searcher;
import io.github.eb4j.SubBook;
import io.github.eb4j.ebview.data.DictionaryEntry;
import io.github.eb4j.ebview.data.IDictionary;
import io.github.eb4j.hook.Hook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Main class to handle EPWING dictionary.
 */
public class EBDictionary implements IDictionary {

    static final Logger LOG = LoggerFactory.getLogger(EBDictionary.class.getName());

    private final SubBook[] subBooks;

    public EBDictionary(final File catalogFile) throws Exception {
        Book eBookDictionary;
        String eBookDirectory = catalogFile.getParent();
        String appendixDirectory;
        if (new File(eBookDirectory, "appendix").isDirectory()) {
            appendixDirectory = new File(eBookDirectory, "appendix").getPath();
        } else {
            appendixDirectory = eBookDirectory;
        }
        try {
            // try dictionary and appendix first.
            eBookDictionary = new Book(eBookDirectory, appendixDirectory);
            LOG.info("Load dictionary with appendix.");
        } catch (EBException ignore) {
            // There may be no appendix, try again with dictionary only.
            try {
                eBookDictionary = new Book(eBookDirectory);
            } catch (EBException e) {
                logEBError(e);
                throw new Exception("EPWING: There is no supported dictionary");
            }
        }
        subBooks = eBookDictionary.getSubBooks();

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

    private enum Mode {
        PREDICTIVE,
        EXACT,
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

    /**
     * Dispose IDictionary. Default is no action.
     */
    @Override
    public void close() throws IOException {
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

        for (SubBook sb : subBooks) {
            String subBookName = sb.getTitle();
            try {
                hook = new EBDictStringHook(sb);
                if (mode.equals(Mode.PREDICTIVE) && sb.hasWordSearch()) {
                    sh = sb.searchWord(word);
                } else if (mode.equals(Mode.EXACT) && sb.hasExactwordSearch()) {
                    sh = sb.searchExactword(word);
                } else {
                    continue;
                }
                while ((searchResult = sh.getNextResult()) != null) {
                    heading = searchResult.getHeading(hook);
                    if (headings.contains(heading)) {
                        continue;
                    }
                    headings.add(heading);
                    article = searchResult.getText(hook);
                    result.add(new DictionaryEntry(heading, article, subBookName));
                }
            } catch (EBException e) {
                logEBError(e);
            }
        }
            return result;
    }

}
