/*
 * EBViewer, a dictionary viewer application.
 * Copyright (C) 2021 Hiroshi Miura.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package io.github.eb4j.ebview.dictionary;

import io.github.eb4j.dsl.DslDictionary;
import io.github.eb4j.dsl.DslResult;
import io.github.eb4j.dsl.visitor.HtmlDslVisitor;
import io.github.eb4j.ebview.data.DictionaryEntry;
import io.github.eb4j.ebview.data.IDictionary;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Dictionary driver for Lingvo DSL format.
 *
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
     * @author Hiroshi Miura
     */
    public static class LingvoDSLDictionary implements IDictionary {

        protected final DslDictionary dictionary;
        private final HtmlDslVisitor htmlVisitor;

        public LingvoDSLDictionary(final File file) throws Exception {
            dictionary = DslDictionary.loadDictionary(file);
            htmlVisitor = new HtmlDslVisitor(file.getParent());
        }

        @Override
        public String getDictionaryName() {
            return dictionary.getDictionaryName();
        }

        /**
         * read article with exact match.
         * @param word
         *            The word to look up in the dictionary
         *
         * @return list of results.
         */
        @Override
        public List<DictionaryEntry> readArticles(final String word) {
            return readEntries(dictionary.lookup(word));
        }

        /**
         * read article with predictive match.
         * @param word
         *            The word to look up in the dictionary
         *
         * @return list of results.
         */
        @Override
        public List<DictionaryEntry> readArticlesPredictive(final String word) {
            return readEntries(dictionary.lookupPredictive(word));
        }

        private List<DictionaryEntry> readEntries(final DslResult dslResult) {
            List<DictionaryEntry> list = new ArrayList<>();
            for (Map.Entry<String, String> e : dslResult.getEntries(htmlVisitor)) {
                DictionaryEntry dictionaryEntry = new DictionaryEntry(e.getKey(), e.getValue(),
                        dictionary.getDictionaryName());
                list.add(dictionaryEntry);
            }
            return list;
        }
    }
}
