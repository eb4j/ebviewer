package io.github.eb4j.ebview;

import io.github.eb4j.Book;
import io.github.eb4j.EBException;
import io.github.eb4j.Result;
import io.github.eb4j.Searcher;
import io.github.eb4j.SubBook;
import io.github.eb4j.hook.Hook;
import io.github.eb4j.hook.HookAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class EBDict {

    static final Logger LOG = LoggerFactory.getLogger(EBDict.class.getName());

    private final SubBook[] subBooks;

    public EBDict(final File eBookDir) throws Exception {
        String eBookDirectory = eBookDir.getPath();
        Book eBookDictionary;
        try {
            // try dictionary and appendix first.
            eBookDictionary = new Book(eBookDirectory, eBookDirectory);
            LOG.info("Load dictionary with appendix.");
        } catch (EBException ignore) {
            // There may be no appendix, try again with dictionary only.
            try {
                eBookDictionary = new Book(eBookDirectory);
            } catch (EBException e) {
                Utils.logEBError(e);
                throw new Exception("EPWING: There is no supported dictionary");
            }
        }
        subBooks = eBookDictionary.getSubBooks();

    }

    /*
     * (non-Javadoc)
     *
     * @see org.omegat.core.dictionaries.IDictionary#readArticle(java.lang.
     * String, java.lang.Object)
     *
     * Returns not the raw text, but the formatted article ready for
     * upstream use (\n replaced with <br>, etc.
     */
    public List<DictionaryEntry> readArticles(final String word) {
        Searcher sh;
        Result searchResult;
        Hook<String> hook;
        String article;
        List<DictionaryEntry> result = new ArrayList<>();

        for (SubBook sb : subBooks) {
            if (sb.hasExactwordSearch()) {
                try {
                    hook = new EBDictStringHook(sb);
                    sh = sb.searchExactword(word);
                    while ((searchResult = sh.getNextResult()) != null) {
                        article = searchResult.getText(hook);
                        result.add(new DictionaryEntry(word, article));
                    }
                } catch (EBException e) {
                    Utils.logEBError(e);
                }
            }
        }

        return result;
    }

    /**
     * EB/EPWING sequence Hook handler.
     */
    public static final class EBDictStringHook extends HookAdapter<String> {

        private final int maxlines;
        private final StringBuffer output = new StringBuffer(16384);
        private int lineNum = 0;
        private boolean narrow = false;
        private int decType;
        // private final ExtFont extFont;
        private final AltCode altCode;

        public EBDictStringHook(final SubBook sb) {
            this(sb, 500);
        }

        public EBDictStringHook(final SubBook sb, final int lines) {
            super();
            // XXX: disabled getting GAIJI and showing as image.
            // becuase of limitation on dictionary pane
            // extFont = sb.getFont();
            maxlines = lines;
            altCode = new AltCode(sb);
        }

        /**
         * clear output line.
         */
        @Override
        public void clear() {
            output.delete(0, output.length());
            lineNum = 0;
        }

        /*
         * get result string.
         */
        @Override
        public String getObject() {
            return output.toString();
        }

        /*
         * Can accept more input?
         */
        @Override
        public boolean isMoreInput() {
            return lineNum < maxlines;
        }

        /**
         * append character.
         *
         * @param ch character
         */
        @Override
        public void append(final char ch) {
            append(Character.toString(ch));
        }

        /**
         * Append article text.
         *
         * @param text string to append
         */
        @Override
        public void append(final String text) {
            if (narrow) {
                output.append(Utils.convertZen2Han(text));
            } else {
                output.append(text);
            }
        }

        /**
         * Append GAIJI text(bitmap).
         *
         * @param code gaiji code referenced to bitmap griff image
         */
        @Override
        public void append(final int code) {
            output.append(altCode.getAltCode(code, narrow));
        }

        /**
         * begin roman alphabet.
         */
        @Override
        public void beginNarrow() {
            narrow = true;
        }

        /**
         * end roman alphabet.
         */
        @Override
        public void endNarrow() {
            narrow = false;
        }

        /**
         * begin subscript.
         */
        @Override
        public void beginSubscript() {
            output.append("<sub>");
        }

        /**
         * end subscript.
         */
        @Override
        public void endSubscript() {
            output.append("</sub>");
        }

        /**
         * begin super script.
         */
        @Override
        public void beginSuperscript() {
            output.append("<sup>");
        }

        /**
         * end super script.
         */
        @Override
        public void endSuperscript() {
            output.append("</sup>");
        }

        /**
         * set indent of line head.
         *
         * @param len size of indent
         */
        @Override
        public void setIndent(final int len) {
            for (int i = 0; i < len; i++) {
                output.append("&nbsp;");
            }
        }

        /**
         * insert new line.
         */
        @Override
        public void newLine() {
            output.append("<br>");
            lineNum++;
        }

        /**
         * set no break.
         */
        @Override
        public void beginNoNewLine() {
            // FIXME: implement me.
        }

        @Override
        public void endNoNewLine() {
            // FIXME
        }

        /**
         * insert em tag.
         */
        @Override
        public void beginEmphasis() {
            output.append("<em>");
        }

        @Override
        public void endEmphasis() {
            output.append("</em>");
        }

        /**
         * insert decoration.
         *
         * @param type decoration type #BOLD #ITALIC
         */
        @Override
        public void beginDecoration(final int type) {
            this.decType = type;
            switch (decType) {
                case BOLD:
                    output.append("<i>");
                    break;
                case ITALIC:
                    output.append("<b>");
                    break;
                default:
                    output.append("<u>");
                    break;
            }
        }

        @Override
        public void endDecoration() {
            switch (decType) {
                case BOLD:
                    output.append("</i>");
                    break;
                case ITALIC:
                    output.append("</b>");
                    break;
                default:
                    output.append("</u>");
                    break;
            }
        }

        @Override
        public void beginUnicode() {
        }

        @Override
        public void endUnicode() {
        }

    }
}
