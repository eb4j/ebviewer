package io.github.eb4j.ebview;
import io.github.eb4j.EBException;
import io.github.eb4j.SubAppendix;
import io.github.eb4j.SubBook;
import io.github.eb4j.ext.UnicodeMap;
import io.github.eb4j.util.HexUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.File;


public class AltCode {

    private final SubAppendix subAppendix;
    private UnicodeMap unicodeMap;

    public AltCode(final SubBook subBook) {
        String title = subBook.getTitle();
        subAppendix = subBook.getSubAppendix();
        try {
            unicodeMap = new UnicodeMap(title, new File(subBook.getBook().getPath()));
        } catch (EBException e) {
            unicodeMap = null;
        }
    }

    public String getAltCode(final int code, final boolean narrow) {
        String str = null;
        // Check DDWIN style unicode map
        if (unicodeMap != null) {
            str = unicodeMap.get(code);
            if (!StringUtils.isBlank(str)) {
                return str;
            }
        }
        // libEB appendix alternation w/ unicode escape support
        if (subAppendix != null) {
            try {
                if (narrow) {
                    str = subAppendix.getNarrowFontAlt(code);
                } else {
                    str = subAppendix.getWideFontAlt(code);
                }
            } catch (EBException ignore) {
            }
            if (!StringUtils.isBlank(str)) {
                return str;
            }
        }
        // no alternation, insert code hex instead.
        if (narrow) {
            return "[GAIJI=n" + HexUtil.toHexString(code) + "]";
        } else {
            return "[GAIJI=w" + HexUtil.toHexString(code) + "]";
        }
    }
}

