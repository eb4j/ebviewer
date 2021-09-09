package io.github.eb4j.ebview.dictionary.epwing;

import io.github.eb4j.EBException;
import io.github.eb4j.ExtFont;
import io.github.eb4j.SubAppendix;
import io.github.eb4j.SubBook;
import io.github.eb4j.ext.UnicodeMap;
import io.github.eb4j.util.HexUtil;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;


/**
 * Gaiji handling class.
 * @author Hiroshi Miura
 */
public class Gaiji {

    private final SubAppendix subAppendix;
    private ExtFont extFont;
    private UnicodeMap unicodeMap;

    public Gaiji(final SubBook subBook) {
        String title = subBook.getTitle();
        try {
            unicodeMap = new UnicodeMap(title, new File(subBook.getBook().getPath()));
        } catch (EBException e) {
            unicodeMap = null;
        }
        subAppendix = subBook.getSubAppendix();
        extFont = subBook.getFont(ExtFont.FONT_16);
    }

    public String getAltCode(final int code, final boolean narrow) {
        String str = null;
        // Check DDWIN style unicode map
        if (unicodeMap != null) {
            if (narrow) {
                str = unicodeMap.getNarrow(code);
            } else {
                str = unicodeMap.getWide(code);
            }
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
        // no alternation, use image.
        if (narrow) {
            try {
                int height = extFont.getFontHeight();
                int width = extFont.getNarrowFontWidth();
                byte[] data = extFont.getNarrowFont(code);
                str = convertImage(data, width, height);
            } catch (EBException | IOException ignore) {
            }
        } else {
            try {
                int height = extFont.getFontHeight();
                int width = extFont.getWideFontWidth();
                str = convertImage(extFont.getWideFont(code), width, height);
            } catch (EBException | IOException ignore) {
            }
        }
        if (!StringUtils.isBlank(str)) {
            return str;
        }
        // last fallback
        if (narrow) {
            str = "[GAIJI=n" + HexUtil.toHexString(code) + "]";
        } else {
            str = "[GAIJI=w" + HexUtil.toHexString(code) + "]";
        }
        return str;
    }

    private String convertImage(final byte[] data, final int width, final int height) throws IOException {
        StringBuilder sb = new StringBuilder("<img src=\"data:image/png;base64,");
        sb.append(Utils.convertImage2Base64(data, width, height));
        sb.append("\"/>");
        return sb.toString();
    }
}

