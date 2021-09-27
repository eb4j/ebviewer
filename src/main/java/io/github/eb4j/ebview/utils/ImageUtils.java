package io.github.eb4j.ebview.utils;

import io.github.eb4j.util.ImageUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;


public final class ImageUtils {
    private ImageUtils() {
    }

    /**
     * convert image data to base64.
     * @param format image format.
     * @param data image data
     * @return base64 string
     * @throws IOException when conversion failed
     */
    public static String convertImage2Base64(final String format, final byte[] data) throws IOException {
        byte[] bytes;
        Base64.Encoder base64Encoder = Base64.getEncoder();
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            final BufferedImage res = ImageIO.read(new ByteArrayInputStream(data));
            ImageIO.write(res, format, baos);
            baos.flush();
            bytes = baos.toByteArray();
        }
        return base64Encoder.encodeToString(bytes);
    }

    /**
     * Convert eb_bitmap to PNG, and convert to Base64 String.
     *
     * @param data  eb_bitmap font data
     * @param width  image width
     * @param height image height
     * @return String Base64 encoded PNG data.
     * @throws IOException when the image is broken or caused error.
     */
    public static String convertMonoGraphic2Base64(final byte[] data, final int width, final int height)
            throws IOException {
        return convertImage2Base64("png", ImageUtil.ebBitmap2BMP(data, width, height));
    }
}
