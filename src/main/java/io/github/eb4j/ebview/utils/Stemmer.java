/*
 * EBViewer, a dictionary viewer application.
 * Copyright (C) 2022 Hiroshi Miura.
 * Copyright (C) 2000-2006 Keith Godfrey, Maxym Mykhalchuk, and Henry Pijffers
 *              2007 Didier Briel, Zoltan Bartko
 *              2008 Alex Buloichik
 *              2015 Didier Briel, Aaron Madlon-Kay*
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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

    private static final String[] EMPTY_STRINGS_LIST = new String[0];

    public Stemmer() {
    }

    public String[] doStem(final String str) {
        if (StringUtils.isBlank(str)) {
            return EMPTY_STRINGS_LIST;
        }
        return tokenizeTextToStringsNoCache(str);
    }

    private static String[] tokenizeTextToStringsNoCache(final String str) {
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
        return tokens.toArray(new String[0]);
    }

}
