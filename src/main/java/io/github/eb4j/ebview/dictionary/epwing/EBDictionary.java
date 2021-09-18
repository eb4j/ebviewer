package io.github.eb4j.ebview.dictionary.epwing;

import io.github.eb4j.Book;
import io.github.eb4j.EBException;
import io.github.eb4j.SubBook;
import io.github.eb4j.ebview.data.IDictionary;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Main class to handle EPWING dictionary.
 */
public class EBDictionary {

    static final Logger LOG = LoggerFactory.getLogger(EBDictionary.class.getName());

    private final List<SubBook> subBooks;

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
            LOG.info("Loading appendix for " + eBookDictionary.getSubBook(0).getTitle());
        } catch (EBException ignore) {
            // There may be no appendix, try again with dictionary only.
            try {
                eBookDictionary = new Book(eBookDirectory);
            } catch (EBException e) {
                throw new Exception("EPWING: There is no supported dictionary");
            }
        }
        subBooks = Arrays.asList(eBookDictionary.getSubBooks());
    }

    public SubBook getSubBook(final int index) {
        return subBooks.get(index);
    }

    public Set<IDictionary> getEBDictionarySubBooks() {
        Set<IDictionary> result = new HashSet<>();
        for (int i = 0, subBooksSize = subBooks.size(); i < subBooksSize; i++) {
            result.add(new EBDictionarySubbook(this, i));
        }
        return result;
    }
}
