package io.github.eb4j.ebview.dictionary.epwing;

import io.github.eb4j.EBException;
import io.github.eb4j.ExtFont;
import io.github.eb4j.SubAppendix;
import io.github.eb4j.SubBook;
import io.github.eb4j.ext.UnicodeMap;
import io.github.eb4j.util.HexUtil;
import org.apache.commons.lang3.StringUtils;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;


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

    /**
     * Convert XBM image to lossless WebP and convert to Base64 String.
     *
     * @param data   XBM data
     * @param height image height
     * @param width  image width
     * @return String Base64 encoded BMP data.
     * @throws IOException when the image is broken or caused error.
     */
    private static String convertImage(final byte[] data, final int height, final int width) throws IOException {
        final BufferedImage res = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int bitPos = 1 << ((x + y * height) & 0x07);
                int pos = (x + y * height) >> 3;
                if ((data[pos] & bitPos) == 0) {
                    res.setRGB(x, y, Color.WHITE.getRGB());
                } else {
                    res.setRGB(x, y, Color.BLACK.getRGB());
                }
            }
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(res, "png", baos);
        baos.flush();
        byte[] bytes = baos.toByteArray();
        baos.close();

        Base64.Encoder base64Encoder = Base64.getEncoder();
        return "<img src=\"data:image/png;base64,"
                + base64Encoder.encodeToString(bytes)
                + "\"></img>";
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
                str = convertImage(data, height, width);
            } catch (EBException | IOException ignore) {
            }
        } else {
            try {
                int height = extFont.getFontHeight();
                int width = extFont.getWideFontWidth();
                str = convertImage(extFont.getWideFont(code), height, width);
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
}

