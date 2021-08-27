package io.github.eb4j.ebview;

/**
 * Dictionary article data POJO class.
 */
public class DictionaryEntry {
    private final String word;
    private String article;

    public DictionaryEntry(final String word, final String article) {
        this.word = word;
        this.article = article;
    }

    public String getWord() {
        return word;
    }

    public String getArticle() {
        return article;
    }
}
