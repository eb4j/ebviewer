package io.github.eb4j.ebview.dictionary.mdict;

import io.github.eb4j.ebview.data.DictionaryEntry;
import io.github.eb4j.ebview.data.IDictionary;
import io.github.eb4j.mdict.MDException;
import io.github.eb4j.mdict.MDictDictionary;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MDX implements IDictionary {

    private MDictDictionary mdictionary;

    public MDX(final File mdxFile) throws MDException, IOException {
        String mdxPath = mdxFile.getPath();
        // String f = mdxPath;
        // if (f.endsWith(".mdx")) {
        //     f = f.substring(0, f.length() - ".mdx".length());
        // }
        // String dictName = f;
        // String parent = mdxFile.getParent();
        // File mddFile = new File(dictName + ".mdd");
        mdictionary = MDictDictionary.loadDicitonary(mdxPath);
    }

    @Override
    public String getDictionaryName() {
        return mdictionary.getTitle();
    }

    /**
     * Read article's text.
     *
     * @param word The word to look up in the dictionary
     * @return List of entries. May be empty, but cannot be null.
     */
    @Override
    public List<DictionaryEntry> readArticles(final String word) throws Exception {
        List<DictionaryEntry> result = new ArrayList<>();
        for (Map.Entry<String, Object> entry: mdictionary.getEntries(word)) {
            String heading = entry.getKey();
            String article = mdictionary.getText((long) entry.getValue());
            result.add(new DictionaryEntry(heading, article, getDictionaryName()));
        }
        return result;
    }

}
