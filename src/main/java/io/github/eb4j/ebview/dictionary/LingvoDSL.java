package io.github.eb4j.ebview.dictionary;

import io.github.eb4j.dsl.DslDictionary;
import io.github.eb4j.dsl.visitor.HtmlDslVisitor;
import io.github.eb4j.ebview.data.DictionaryEntry;
import io.github.eb4j.ebview.data.IDictionary;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Dictionary driver for Lingvo DSL format.
 *
 * Lingvo DSL format described in Lingvo help. See also
 * http://www.dsleditor.narod.ru/art_03.htm(russian).
 *
 * @author Alex Buloichik
 * @author Aaron Madlon-Kay
 * @author Hiroshi Miura
 */
public class LingvoDSL implements IDictionaryFactory {

    @Override
    public boolean isSupportedFile(final File file) {
        return file.getPath().endsWith(".dsl") || file.getPath().endsWith(".dsl.dz");
    }

    @Override
    public Set<IDictionary> loadDict(final File file) throws Exception {
        Set<IDictionary> result = new HashSet<>();
        result.add(new LingvoDSLDictionary(file));
        return result;
    }

    /**
     * Dictionary implementation for Lingvo DSL format.
     *
     * @author Alex Buloichik
     * @author Aaron Madlon-Kay
     * @author Hiroshi Miura
     */
    public static class LingvoDSLDictionary implements IDictionary {

        protected final DslDictionary dictionary;
        private final String bookName;
        private final HtmlDslVisitor htmlDslVisitor;

        public LingvoDSLDictionary(final File file) throws Exception {
            String fileName = file.getName();
            if (fileName.endsWith(".dz")) {
                bookName = fileName.substring(0, fileName.length() - 7);
            } else {
                bookName = fileName.substring(0, fileName.length() - 4);
            }
            dictionary = DslDictionary.loadDictionary(file);
            htmlDslVisitor = new HtmlDslVisitor();
        }

        @Override
        public String getDictionaryName() {
            return bookName;
        }

        @Override
        public List<DictionaryEntry> readArticles(final String word) {
            return dictionary.lookup(word).getEntries(htmlDslVisitor).stream()
                    .map(e -> new DictionaryEntry(e.getKey(), e.getValue(), bookName))
                    .collect(Collectors.toList());
        }

        @Override
        public List<DictionaryEntry> readArticlesPredictive(final String word) {
            return dictionary.lookupPredictive(word).getEntries(htmlDslVisitor).stream()
                    .map(e -> new DictionaryEntry(e.getKey(), e.getValue(), bookName))
                    .collect(Collectors.toList());
        }

        /**
         * Dispose IDictionary. Default is no action.
         */
        @Override
        public void close() {
        }
    }
}
