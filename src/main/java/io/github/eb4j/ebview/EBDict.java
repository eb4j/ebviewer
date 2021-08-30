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


/**
 * Main class to handle EPWING dictionary.
 */
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
                logEBError(e);
                throw new Exception("EPWING: There is no supported dictionary");
            }
        }
        subBooks = eBookDictionary.getSubBooks();

    }

    /**
     * convert Zenkaku alphabet to Hankaku.
     * <p>
     * convert (\uFF01 - \uFF5E) to (\u0021- \u007E) and \u3000 to \u0020
     *
     * @param text source text with zenkaku.
     * @return String converted
     */
    private static String convertZen2Han(final String text) {
        StringBuilder result = new StringBuilder(text.length());
        for (int i = 0; i < text.length(); i++) {
            int cp = text.codePointAt(i);
            if (0xFF00 < cp && cp < 0xFF5F) {
                result.append((char) (cp - 0xFEE0));
            } else if (cp == 0x3000) {
                result.append("\u0020");
            } else {
                result.appendCodePoint(cp);
            }
        }
        return result.toString();
    }

    private static void logEBError(final EBException e) {
        switch (e.getErrorCode()) {
            case EBException.CANT_READ_DIR:
                LOG.warn("EPWING error: cannot read directory:" + e.getMessage());
                break;
            case EBException.DIR_NOT_FOUND:
                LOG.warn("EPWING error: cannot found directory:" + e.getMessage());
            default:
                LOG.warn("EPWING error: " + e.getMessage());
                break;
        }
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
            try {
                hook = new EBDictStringHook(sb);
                if (sb.hasWordSearch()) {
                    sh = sb.searchWord(word);
                } else if (sb.hasExactwordSearch()) {
                    sh = sb.searchExactword(word);
                } else {
                    continue;
                }
                while ((searchResult = sh.getNextResult()) != null) {
                    article = searchResult.getText(hook);
                    result.add(new DictionaryEntry(word, article));
                }
            } catch (EBException e) {
                logEBError(e);
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
        private final Gaiji gaiji;

        public EBDictStringHook(final SubBook sb) {
            this(sb, 500);
        }

        public EBDictStringHook(final SubBook sb, final int lines) {
            super();
            // XXX: disabled getting GAIJI and showing as image.
            // becuase of limitation on dictionary pane
            // extFont = sb.getFont();
            maxlines = lines;
            gaiji = new Gaiji(sb);
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
                output.append(convertZen2Han(text));
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
            output.append(gaiji.getAltCode(code, narrow));
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
