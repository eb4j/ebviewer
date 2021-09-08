package io.github.eb4j.ebview.dictionary.lingvo;

import io.github.eb4j.ebview.data.DictionaryData;
import io.github.eb4j.ebview.data.DictionaryEntry;
import io.github.eb4j.ebview.data.IDictionary;
import org.apache.commons.io.input.BOMInputStream;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;

/**
 * Dictionary implementation for Lingvo DSL format.
 *
 * Lingvo DSL format described in Lingvo help. See also
 * http://www.dsleditor.narod.ru/art_03.htm(russian).
 *
 * @author Alex Buloichik
 * @author Aaron Madlon-Kay
 * @author Hiroshi Miura
 */
public class LingvoDSLDictionary implements IDictionary {

    protected final DictionaryData<String> data;

    private final String bookName;

    @SuppressWarnings("visibilitymodifier")
    static class Re {
        public String regex;
        public String replacement;

        Re(final String regex, final String replacement) {
            this.regex = regex;
            this.replacement = replacement;
        }
    }

    protected final List<Re> reList = new ArrayList<>();

    public LingvoDSLDictionary(final File file) throws Exception {
        data = new DictionaryData<>();
        String fileName = file.getName();
        if (fileName.endsWith(".dz")) {
            bookName = fileName.substring(0, fileName.length() - 7);
        } else {
            bookName = fileName.substring(0, fileName.length() - 4);
        }
        reList.add(new Re("\\[b\\](.+?)\\[/b\\]", "<strong>$1</strong>"));
        reList.add(new Re("\\[i\\](.+?)\\[/i\\]", "<span style='font-style: italic'>$1</span>"));
        reList.add(new Re("\\[trn\\](.+?)\\[/trn\\]", "<br>&nbsp;-&nbsp;$1"));
        reList.add(new Re("\\[t\\](.+?)\\[/t\\]", "$1&nbsp;"));
        readDslFile(file);
    }

    @SuppressWarnings("avoidinlineconditionals")
    private void readDslFile(final File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            // Un-gzip if necessary
            InputStream is = file.getName().endsWith(".dz") ? new GZIPInputStream(fis, 8192) : fis;
            try (BOMInputStream bis = new BOMInputStream(is)) {
                // Detect charset
                Charset charset = bis.hasBOM() ? StandardCharsets.UTF_8 : StandardCharsets.UTF_16;
                try (InputStreamReader isr = new InputStreamReader(bis, charset);
                     BufferedReader reader = new BufferedReader(isr)) {
                    loadData(reader.lines());
                }
            }
        }
    }

    private String replaceTag(final String line) {
        String result = line;
        for (Re re : reList) {
            result = result.replaceAll(re.regex, re.replacement);
        }
        return result.replaceAll("\\[\\[(.+?)\\]\\]", "[$1]");
    }

    private void loadData(final Stream<String> stream) {
        StringBuilder word = new StringBuilder();
        StringBuilder trans = new StringBuilder();
        stream.filter(line -> !line.isEmpty() && !line.startsWith("#"))
                .forEach(line -> {
                    line = replaceTag(line);
                    if (Character.isWhitespace(line.codePointAt(0))) {
                        trans.append(line.trim()).append('\n');
                    } else {
                        if (word.length() > 0) {
                            data.add(word.toString(), trans.toString());
                            word.setLength(0);
                            trans.setLength(0);
                        }
                        word.append(line);
                    }
                });
        if (word.length() > 0) {
            data.add(word.toString(), trans.toString());
        }
        data.done();
    }

    @Override
    public List<DictionaryEntry> readArticles(final String word) {
        return data.lookUp(word).stream().map(e -> new DictionaryEntry(e.getKey(), e.getValue(), bookName))
                .collect(Collectors.toList());
    }

    @Override
    public List<DictionaryEntry> readArticlesPredictive(final String word) {
        return data.lookUpPredictive(word).stream().map(e -> new DictionaryEntry(e.getKey(), e.getValue(), bookName))
                .collect(Collectors.toList());
    }

    /**
     * Dispose IDictionary. Default is no action.
     */
    @Override
    public void close() throws IOException {
    }
}
