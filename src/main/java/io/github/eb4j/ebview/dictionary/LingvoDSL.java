package io.github.eb4j.ebview.dictionary;

import io.github.eb4j.ebview.data.IDictionary;
import io.github.eb4j.ebview.dictionary.lingvo.LingvoDSLDictionary;

import java.io.File;

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
    public IDictionary loadDict(final File file) throws Exception {
        return new LingvoDSLDictionary(file);
    }

}
