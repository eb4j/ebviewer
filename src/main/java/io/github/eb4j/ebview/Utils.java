package io.github.eb4j.ebview;

import io.github.eb4j.EBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

public final class Utils {

    static final Logger LOG = LoggerFactory.getLogger(EBDict.class.getName());

    private Utils() {
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
    static String convert2Image(final byte[] data, final int height, final int width) throws IOException {
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
        ImageIO.write(res, "bmp", baos);
        baos.flush();
        byte[] bytes = baos.toByteArray();
        baos.close();

        Base64.Encoder base64Encoder = Base64.getEncoder();
        return "<img src=\"data:image/bmp;base64,"
                + base64Encoder.encodeToString(bytes)
                + "\"></img>";
    }

    /**
     * convert unicode escape sequence to unicode code.
     */
    static String getUnicode(final String text) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < text.length(); i++) {
            int cp = text.codePointAt(i);
        }
        return sb.toString();
    }

    /**
     * convert Zenkaku alphabet to Hankaku.
     * <p>
     * convert (\uFF01 - \uFF5E) to (\u0021- \u007E) and \u3000 to \u0020
     *
     * @param text source text with zenkaku.
     * @return String converted
     */
    static String convertZen2Han(final String text) {
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

    static void logEBError(final EBException e) {
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
}
