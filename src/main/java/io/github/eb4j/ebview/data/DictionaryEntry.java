package io.github.eb4j.ebview.data;

/**
 * Dictionary article data class.
 */
public class DictionaryEntry {
    private final String word;
    private String article;

    public DictionaryEntry(final String word, final String article) {
        this.word = word;
        this.article = article;
    }

    /**
     * return entry word.
     * @return entry word.
     */
    public String getWord() {
        return word;
    }

    /**
     * return article.
     * @return article.
     */
    public String getArticle() {
        return article;
    }
}
