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
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;

/**
 * Dictionary implementation for Lingvo DSL format.
 * <p>
 * Lingvo DSL format described in Lingvo help. See also
 * http://www.dsleditor.narod.ru/art_03.htm(russian).
 *
 * @author Alex Buloichik
 * @author Aaron Madlon-Kay
 * @author Hiroshi Miura
 */
public class LingvoDSLDictionary implements IDictionary {

    protected final DictionaryData<String> data;
    private final String dictionaryDir;
    private final String bookName;


    public LingvoDSLDictionary(final File file) throws Exception {
        data = new DictionaryData<>();
        String fileName = file.getName();
        if (fileName.endsWith(".dz")) {
            bookName = fileName.substring(0, fileName.length() - 7);
        } else {
            bookName = fileName.substring(0, fileName.length() - 4);
        }
        dictionaryDir = file.getParentFile().getAbsolutePath();
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

    private void loadData(final Stream<String> stream) {
        StringBuilder word = new StringBuilder();
        StringBuilder trans = new StringBuilder();
        stream.filter(line -> !line.isEmpty() && !line.startsWith("#"))
                .map(LingvoDSLTag::replaceTag)
                .forEach(line -> {
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
    public String getDictionaryName() {
        return bookName;
    }

    @Override
    public List<DictionaryEntry> readArticles(final String word) {
        return data.lookUp(word).stream().map(e -> new DictionaryEntry(e.getKey(), e.getValue().replaceAll("@dir@", dictionaryDir), bookName))
                .collect(Collectors.toList());
    }

    @Override
    public List<DictionaryEntry> readArticlesPredictive(final String word) {
        return data.lookUpPredictive(word).stream().map(e -> new DictionaryEntry(e.getKey(), e.getValue().replaceAll("@dir@", dictionaryDir), bookName))
                .collect(Collectors.toList());
    }

    /**
     * Dispose IDictionary. Default is no action.
     */
    @Override
    public void close() {
    }

    @SuppressWarnings("visibilitymodifier")
    static class RE {
        public Pattern pattern;
        public String replacement;

        RE(final String regex, final String replacement) {
            pattern = Pattern.compile(regex);
            this.replacement = replacement;
        }
    }

    static class LingvoDSLTag {
        private static final List<RE> RE_LIST;

        static String replaceTag(final String line) {
            String result = line;
            for (RE re : RE_LIST) {
                result = re.pattern.matcher(result).replaceAll(re.replacement);
            }
            return result;
        }

        /**
         * Initialize regex patterns as an immutable list.
         */
        static {
            List<RE> reList = new ArrayList<>();
            reList.add(new RE("\\[\\[(.+?)\\]\\]", "&#91;$1&#93;"));
            reList.add(new RE("\\\\\\[", "&#91;"));
            reList.add(new RE("\\\\\\]", "&#93;"));
            reList.add(new RE("\\[b\\](.+?)\\[/b\\]", "<span style='font-style: bold'>$1</span>"));
            reList.add(new RE("\\[i\\](.+?)\\[/i\\]", "<span style='font-style: italic'>$1</span>"));
            reList.add(new RE("\\[trn\\](.+?)\\[/trn\\]", "$1"));
            reList.add(new RE("\\[t\\](.+?)\\[/t\\]", "$1&nbsp;"));
            reList.add(new RE("\\[br\\]", "<br/>"));
            // Green is default color in Lingvo
            reList.add(new RE("\\[c\\](.+?)\\[/c\\]", "<span style='color:green'>$1</span>"));
            // The following line tries to replace [c value]text[/c] with text colored as per the value.
            // Since the color names are plain words like 'red', or 'blue', or 'steelgray' etc.,
            // FIXME: I use the ([a-z]+?) regular expression, but am not sure if it is correct.
            reList.add(new RE("\\[c\\s([a-z]+?)\\](.+?)\\[/c\\]", "<span style='color:$1'>$2</span>"));
            reList.add(new RE("\\[com\\]", ""));
            reList.add(new RE("\\[/com\\]", ""));
            reList.add(new RE("\\[ex\\]", ""));
            reList.add(new RE("\\[/ex\\]", ""));
            reList.add(new RE("\\[lang\\]", ""));
            reList.add(new RE("\\[/lang\\]", ""));
            reList.add(new RE("\\[m\\](.+?)\\[/m\\]", "$1"));
            reList.add(new RE("\\[m1\\](.+?)\\[/m\\]", "<div style=\"text-indent: 30px\">$1</div>"));
            reList.add(new RE("\\[m2\\](.+?)\\[/m\\]", "<div style=\"text-indent: 60px\">$1</div>"));
            reList.add(new RE("\\[m3\\](.+?)\\[/m\\]", "<div style=\"text-indent: 90px\">$1</div>"));
            reList.add(new RE("\\[m4\\](.+?)\\[/m\\]", "<div style=\"text-indent: 90px\">$1</div>"));
            reList.add(new RE("\\[m5\\](.+?)\\[/m\\]", "<div style=\"text-indent: 90px\">$1</div>"));
            reList.add(new RE("\\[m6\\](.+?)\\[/m\\]", "<div style=\"text-indent: 90px\">$1</div>"));
            reList.add(new RE("\\[m7\\](.+?)\\[/m\\]", "<div style=\"text-indent: 90px\">$1</div>"));
            reList.add(new RE("\\[m8\\](.+?)\\[/m\\]", "<div style=\"text-indent: 90px\">$1</div>"));
            reList.add(new RE("\\[m9\\](.+?)\\[/m\\]", "<div style=\"text-indent: 90px\">$1</div>"));
            reList.add(new RE("\\[p\\]", ""));
            reList.add(new RE("\\[/p\\]", ""));
            reList.add(new RE("\\[preview\\]", ""));
            reList.add(new RE("\\[/preview\\]", ""));
            reList.add(new RE("\\[ref\\]", ""));
            reList.add(new RE("\\[/ref\\]", ""));
            reList.add(new RE("\\[s\\](.+?\\.(wav|WAV))\\[/s\\]", "<a href=\"file://@dir@/$1\">SOUND: $1</a>"));
            reList.add(new RE("\\[s\\](.+?\\.(jpg|JPG))\\[/s\\]", "<img src=\"file://@dir@/$1\"/>"));
            reList.add(new RE("\\[s\\](.+?\\.(png|PNG))\\[/s\\]", "<img src=\"file://@dir@/$1\"/>"));
            reList.add(new RE("\\[video\\](.+?)\\[/video\\]", "<a href=\"file://@dir@/$1\">VIDEO: $1</a>"));
            reList.add(new RE("\\[s\\](.+?)\\[/s\\]", "<a href=\"file://@dir@/$1\">MEDIA: $1</a>"));
            reList.add(new RE("\\[sub\\](.+?)\\[/sub\\]", "<sub>$1</sub>"));
            reList.add(new RE("\\[sup\\](.+?)\\[/sup\\]", "<sup>$1</sup>"));
            reList.add(new RE("\\[trn1\\]", ""));
            reList.add(new RE("\\[/trn1\\]", ""));
            reList.add(new RE("\\[trs\\]", ""));
            reList.add(new RE("\\[/trs\\]", ""));
            // FIXME: In the following two lines, the exclamation marks are escaped. Maybe, it is superfluous.
            reList.add(new RE("\\[\\!trs\\]", ""));
            reList.add(new RE("\\[/\\!trs\\]", ""));
            reList.add(new RE("\\[u\\](.+?)\\[/u\\]",
                    "<span style='text-decoration:underline'>$1</span>"));
            reList.add(new RE("\\[url\\](.+?)\\[/url\\]", "<a href='$1'>$1</a>"));
            // The following line tries to replace a letter surrounded by ['][/'] tags (indicating stress)
            // with a red letter (the default behavior in Lingvo).
            reList.add(new RE("\\['\\].\\[/'\\]", "<span style='color:red'>$1</span>"));
            // FIXME: In the following two lines, the asterisk symbols are escaped. Maybe, it is superfluous.
            reList.add(new RE("\\[\\*\\]", ""));
            reList.add(new RE("\\[/\\*\\]", ""));
            RE_LIST = Collections.unmodifiableList(reList);
        }
    }
}
