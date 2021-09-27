package io.github.eb4j.ebview.utils;

import org.apache.commons.lang3.StringUtils;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;

/**
 * Methods for tokenize string.
 *
 * @author Keith Godfrey
 * @author Maxym Mykhalchuk
 * @author Henry Pijffers (henry.pijffers@saxnot.com)
 * @author Didier Briel
 * @author Zoltan Bartko - bartkozoltan@bartkozoltan.com
 * @author Alex Buloichik
 * @author Aaron Madlon-Kay
 */
public class Stemmer {

    public static final String[] EMPTY_STRINGS_LIST = new String[0];

    public Stemmer() {
    }

    public String[] doStem(String str) {
        if (StringUtils.isBlank(str)) {
            return EMPTY_STRINGS_LIST;
        }
        return tokenizeTextToStringsNoCache(str);
    }

    private static String[] tokenizeTextToStringsNoCache(String str) {
        if (StringUtils.isBlank(str)) {
            return EMPTY_STRINGS_LIST;
        }
        // create a new token list
        List<String> tokens = new ArrayList<>(64);
        // get a word breaker
        BreakIterator breaker = new WordIterator();
        breaker.setText(str);
        int start = breaker.first();
        for (int end = breaker.next(); end != BreakIterator.DONE; start = end, end = breaker.next()) {
            String tokenStr = str.substring(start, end);
            boolean word = false;
            for (int cp, i = 0; i < tokenStr.length(); i += Character.charCount(cp)) {
                cp = tokenStr.codePointAt(i);
                if (Character.isLetter(cp)) {
                    word = true;
                    break;
                }
            }
            if (word) {
                tokens.add(tokenStr);
            }
        }
        return tokens.toArray(new String[tokens.size()]);
    }

}
