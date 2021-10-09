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
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
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

    // An ordered list of Pair of Regex pattern and replacement string
    private static final TreeMap<Pattern, String> TAG_REPLACEMENTS = new TreeMap<>(
            Comparator.comparing(Pattern::pattern));

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

    private static boolean testLine(final String line) {
        return !line.isEmpty() && !line.startsWith("#");
    }

    private void loadData(final Stream<String> stream) {
        StringBuilder word = new StringBuilder();
        StringBuilder trans = new StringBuilder();
        stream.filter(LingvoDSLDictionary::testLine)
              .map(LingvoDSLDictionary::replaceTag)
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
        return data.lookUp(word).stream()
                .map(e -> new DictionaryEntry(e.getKey(), e.getValue()
                        .replaceAll("@dir@", dictionaryDir), bookName))
                .collect(Collectors.toList());
    }

    @Override
    public List<DictionaryEntry> readArticlesPredictive(final String word) {
        return data.lookUpPredictive(word).stream()
                .map(e -> new DictionaryEntry(e.getKey(), e.getValue()
                        .replaceAll("@dir@", dictionaryDir), bookName))
                .collect(Collectors.toList());
    }

    /**
     * Dispose IDictionary. Default is no action.
     */
    @Override
    public void close() {
    }

    private static String replaceTag(final String line) {
        String result = line;
        for (Map.Entry<Pattern, String> entry : TAG_REPLACEMENTS.entrySet()) {
            result = entry.getKey().matcher(result).replaceAll(entry.getValue());
        }
        return result;
    }

    static {
        // TODO: real parser.
        // DSL language is not context-free:
        // it allows arbitrary nesting (and also the meaning of e.g. [/m] is context-dependent)
        // so this approach of replacing regex patterns is doomed to fail at at least some cases.
        // and may leave some [tag].
        // The only way to handle this language robustly is to make a real parser.
        // ---
        // following 3 lines replaces "[[...]]" and "\[..\]" into "[...]"
        // but using HTML body reference to avoid further match.
        TAG_REPLACEMENTS.put(Pattern.compile("\\[\\[(?<content>.+?)]]"), "&#91;${content}&#93;");
        TAG_REPLACEMENTS.put(Pattern.compile(Pattern.quote("\\[")), "&#91;");
        TAG_REPLACEMENTS.put(Pattern.compile(Pattern.quote("\\]")), "&#93;");
        // styling tags
        TAG_REPLACEMENTS.put(Pattern.compile("\\[b](?<content>.+?)\\[/b]"), "<strong>${content}</strong>");
        TAG_REPLACEMENTS.put(Pattern.compile(
                "\\[i](?<content>.+?)\\[/i]"), "<span style='font-style: italic'>${content}</span>");
        TAG_REPLACEMENTS.put(Pattern.compile("\\[t](?<content>.+?)\\[/t]"), "${content}&nbsp;");
        TAG_REPLACEMENTS.put(Pattern.compile(
                "\\[c](?<content>.+?)\\[/c]"), "<span style='color:green'>${content}</span>");
        TAG_REPLACEMENTS.put(Pattern.compile("\\[u](?<content>.+?)\\[/u]"),
                "<span style='text-decoration:underline'>${content}</span>");
        // The following line tries to replace [c value]text[/c] with text colored as per the value.
        // Since the color names are plain words like 'red', or 'blue', or 'steelgray' etc.,
        TAG_REPLACEMENTS.put(Pattern.compile(
                "\\[c\\s(?<color>[a-z]+?)](?<content>.+?)\\[/c]"), "<span style='color:${color}'>${content}</span>");
        TAG_REPLACEMENTS.put(Pattern.compile("\\[sub](?<content>.+?)\\[/sub]"), "<sub>${content}</sub>");
        TAG_REPLACEMENTS.put(Pattern.compile("\\[sup](?<content>.+?)\\[/sup]"), "<sup>${content}</sup>");
        // line feed and indents
        TAG_REPLACEMENTS.put(Pattern.compile(Pattern.quote("[br]")), "<br/>");
        // ignore tag 'm" but "m1" to indent 1 level, "m2" to indent 2 level and "m3" and more to indent 3 level.
        TAG_REPLACEMENTS.put(Pattern.compile(
                "\\[m1](?<content>.+?)\\[/m]"), "<p style=\"text-indent: 30px\">${content}</p>");
        TAG_REPLACEMENTS.put(Pattern.compile(
                "\\[m2](?<content>.+?)\\[/m]"), "<p style=\"text-indent: 60px\">${content}</p>");
        TAG_REPLACEMENTS.put(Pattern.compile(
                "\\[(m3|m4|m5|m6|m7|m8|m9)](?<content>.+?)\\[/m]"), "<p style=\\\"text-indent: 90px\">${content}</p>");
        // external link may launch external browser
        TAG_REPLACEMENTS.put(Pattern.compile("\\[url](?<link>.+?)\\[/url]"), "<a href='${link}'>LINK</a>");
        // The following line tries to replace a letter surrounded by ['][/'] tags (indicating stress)
        // with a red letter (the default behavior in Lingvo).
        TAG_REPLACEMENTS.put(
                Pattern.compile("\\['](?<content>.+?)\\[/']"), "<span style='color:red'>${content}</span>");
        TAG_REPLACEMENTS.put(Pattern.compile(
                "\\[s](?<media>.+?\\.wav)\\[/s]"), "<a href=\"file://@dir@/${media}\">SOUND: $1</a>");
        TAG_REPLACEMENTS.put(Pattern.compile("\\[s](?<media>.+?\\.jpg)\\[/s]"), "<img src=\"file://@dir@/${media}\"/>");
        TAG_REPLACEMENTS.put(Pattern.compile("\\[s](?<media>.+?\\.png)\\[/s]"), "<img src=\"file://@dir@/${media}\"/>");
        TAG_REPLACEMENTS.put(Pattern.compile(
                "\\[video](?<media>.+?)\\[/video]"), "<a href=\"file://@dir@/${media}\">VIDEO</a>");
        TAG_REPLACEMENTS.put(Pattern.compile(
                "\\[s](?<media>.+?)\\[/s]"), "<a href=\"file://@dir@/${media}\">MEDIA: ${media}</a>");
        // silently ignored these tags that can be arbitrary nested.
        String[] ignoreTags = {"\\*", "m", "com", "ex", "lang", "p", "preview", "ref", "trn", "trn1", "trs", "!trs"};
        for (String tag : ignoreTags) {
            TAG_REPLACEMENTS.put(Pattern.compile("\\[(?<tag>" + tag + ")](?<content>.+?)\\[/\\k<tag>]"), "${content}");
        }
    }
}
