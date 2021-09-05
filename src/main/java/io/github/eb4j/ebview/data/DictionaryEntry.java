package io.github.eb4j.ebview.data;

/**
 * Dictionary article data class.
 */
public class DictionaryEntry {
    private final String word;
    private String article;
    private String dictName;

    public DictionaryEntry(final String word, final String article, final String dictionary) {
        this.word = word;
        this.article = article;
        dictName = dictionary;
    }

    public String getDictName() {
        return dictName;
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
