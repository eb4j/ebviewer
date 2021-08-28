package io.github.eb4j.ebview;

import java.util.List;

import static io.github.eb4j.ebview.StringUtils.sliceString;

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
     * return list of article.
     * It returns a list of articles, that is sliced for styling change.
     * @return List of articles.
     */
    public List<String> getArticle() {
        return sliceString(article);
    }
}
