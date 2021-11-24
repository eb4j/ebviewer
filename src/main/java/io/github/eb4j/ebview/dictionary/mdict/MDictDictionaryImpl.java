package io.github.eb4j.ebview.dictionary.mdict;

import io.github.eb4j.ebview.data.DictionaryEntry;
import io.github.eb4j.ebview.data.IDictionary;
import io.github.eb4j.ebview.utils.ImageUtils;
import io.github.eb4j.mdict.MDException;
import io.github.eb4j.mdict.MDictDictionary;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Safelist;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MDictDictionaryImpl implements IDictionary {

    private final MDictDictionary mdictionary;
    private final MDictDictionary mData;

    public MDictDictionaryImpl(final File mdxFile) throws MDException, IOException {
        String mdxPath = mdxFile.getPath();
        mdictionary = MDictDictionary.loadDicitonary(mdxPath);
        MDictDictionary temp = null;
        try {
            if (mdictionary.getMdxVersion().equals("2.0")) {
                temp = MDictDictionary.loadDictionaryData(mdxPath);
            }
        } catch (MDException | IOException ignored) {
        }
        mData = temp;
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
            addEntry(result, entry);
        }
        return result;
    }

    /**
     * Read article's text. Matching is predictive, so e.g. supplying "term"
     * will return articles for "term", "terminology", "termite", etc. The
     * default implementation simply calls {@link #readArticles(String)} for
     * backwards compatibility.
     *
     * @param word The word to look up in the dictionary
     * @return List of entries. May be empty, but cannot be null.
     */
    @Override
    public List<DictionaryEntry> readArticlesPredictive(final String word) throws Exception {
        List<DictionaryEntry> result = new ArrayList<>();
        for (Map.Entry<String, Object> entry: mdictionary.getEntriesPredictive(word)) {
            addEntry(result, entry);
        }
        return result;
    }

    private void addEntry(final List<DictionaryEntry> result, final Map.Entry<String, Object> entry)
            throws MDException {
        if (entry.getValue() instanceof Long) {
            result.add(new DictionaryEntry(entry.getKey(),
                    retrieveDataAndUpdateLink(cleaHtmlArticle(mdictionary.getText((Long) entry.getValue()))),
                    getDictionaryName()));
        } else {
            Long[] values = (Long[]) entry.getValue();
            for (int i = 0; i < values.length; i++) {
                result.add(new DictionaryEntry(entry.getKey(),
                        retrieveDataAndUpdateLink(cleaHtmlArticle(mdictionary.getText(values[i]))),
                        getDictionaryName()));
            }
        }
    }

    private String cleaHtmlArticle(final String mdictHtmlText) {
        Safelist whitelist = new Safelist();
        whitelist.addTags("b", "br");
        whitelist.addAttributes("font", "color", "face");
        whitelist.addAttributes("img", "src");
        whitelist.addAttributes("a", "href");
        return Jsoup.clean(mdictHtmlText, whitelist);
    }

    private String retrieveDataAndUpdateLink(final String mdictHtmlText) {
        Document document = Jsoup.parse(mdictHtmlText);
        // Support embeded image
        try {
            Elements elements = document.select("img[src]");
            for (Element element: elements) {
                String linkUrl = element.attr("src");
                if (linkUrl.startsWith("file://pic/")) {
                    String targetKey = linkUrl.substring(6);
                    byte[] rawData = getRawData(targetKey);
                    StringBuffer stringBuffer = new StringBuffer();
                    stringBuffer.append("data:image/png;base64,");
                    stringBuffer.append(ImageUtils.convertImage2Base64("png", rawData));
                    element.attr("src", stringBuffer.toString());
                }
            }
        } catch (MDException | IOException e) {
            e.printStackTrace();
        }
        // Support sound
        try {
            Elements elements = document.select("a[href]");
            for (Element element: elements) {
                String linkUrl = element.attr("href");
                if (linkUrl.startsWith("sound://audio/")) {
                    String targetKey = linkUrl.substring(7);
                    byte[] rawData = getRawData(targetKey);
                    File tmpAudioFile = File.createTempFile("ebviewer", ".mp3");
                    tmpAudioFile.deleteOnExit();
                    try (FileOutputStream outputStream = new FileOutputStream(tmpAudioFile)) {
                        outputStream.write(rawData);
                    }
                    element.attr("href", "file://" + tmpAudioFile.toPath());
                }
            }
        } catch (IOException | MDException e) {
            e.printStackTrace();
        }
        return document.outerHtml();
    }

    private byte[] getRawData(final String targetKey) throws MDException {
        byte[] result = null;
        for (Map.Entry<String, Object> entry: mData.getEntries(targetKey)) {
            if (entry.getKey().equals(targetKey)) {
               Object value = entry.getValue();
               if (value instanceof Long) {
                   result = mData.getData((Long) value);
                   break;
               }
            }
        }
        return result;
    }
}
