package io.github.eb4j.ebview;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

public class StringUtils {
    static final Set<Integer> SYMBOLS = new TreeSet<>();
    static {
        SYMBOLS.add(0x02c6);
        SYMBOLS.add(0x02c7);
        SYMBOLS.add(0x02c9);
        SYMBOLS.add(0x02ca);
        SYMBOLS.add(0x02cb);
        SYMBOLS.add(0x02cd);
        SYMBOLS.add(0x02ce);
        SYMBOLS.add(0x02cf);
        SYMBOLS.add(0x02d7);
        SYMBOLS.add(0x02d9);
        SYMBOLS.add(0x02dc);
        SYMBOLS.add(0x02ec);
        SYMBOLS.add(0x02f4);
        SYMBOLS.add(0x02f7);
        SYMBOLS.add(0x223c);
        SYMBOLS.add(0x2024);
        SYMBOLS.add(0x223d);
        SYMBOLS.add(0xa788);
    }

    public static List<String> sliceString(final String article) {
        List<String> result = new ArrayList<>();
        boolean status = false;
        StringBuilder sb = new StringBuilder();
        for (int c: article.codePoints().toArray()) {
            if (status == StringUtils.isSymbol(c)) {
                sb.append(Character.toString(c));
            } else {
                status = StringUtils.isSymbol(c);
                result.add(sb.toString());
                sb = new StringBuilder(Character.toString(c));
            }
        }
        result.add(sb.toString());
        return result;
    }

    public static boolean isSymbol(int c) {
        return 0x02e5 <= c && c <= 0x02e9 || 0xa708 <= c && c <= 0xa716 || SYMBOLS.contains(c);
    }
}
