package io.github.eb4j.ebview;


public class DictionaryEntry {
    private final String word;
    private String article;

    public DictionaryEntry(String word, String article) {
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
