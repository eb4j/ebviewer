package io.github.eb4j.ebview.dictionary;

import io.github.eb4j.ebview.data.IDictionary;
import io.github.eb4j.ebview.dictionary.lingvo.LingvoDSLDictionary;

import java.io.File;

public class LingvoDSL implements IDictionaryFactory {

    @Override
    public boolean isSupportedFile(File file) {
        return file.getPath().endsWith(".dsl") || file.getPath().endsWith(".dsl.dz");
    }

    @Override
    public IDictionary loadDict(File file) throws Exception {
        return new LingvoDSLDictionary(file);
    }

}
