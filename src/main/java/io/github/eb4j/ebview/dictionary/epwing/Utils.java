package io.github.eb4j.ebview.dictionary.epwing;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;

public final class Utils {
    private Utils() {
    }

    protected static byte[] bitmap2BMP(final byte[] data, final int width, final int height) {
        final int BMP_PREAMBLE_LENGTH = 62;
        final byte[] bmpPreamble = new byte[] {
                // Type
                'B', 'M',
                // File size (set at run time)
                0x00, 0x00, 0x00, 0x00,
                // Reserved
                0x00, 0x00, 0x00, 0x00,
                // offset of bitmap bits part
                0x3e, 0x00, 0x00, 0x00,
                // size of bitmap info part
                0x28, 0x00, 0x00, 0x00,
                // width (set at run time)
                0x00, 0x00, 0x00, 0x00,
                // height (set at run time)
                0x00, 0x00, 0x00, 0x00,
                // planes
                0x01, 0x00,
                // bits per pixsels
                0x01, 0x00,
                // compression mode
                0x00, 0x00, 0x00, 0x00,
                // size of bitmap bits part (set at run time)
                0x00, 0x00, 0x00, 0x00,
                // X pixels per meter
                0x6d, 0x0b, 0x00, 0x00,
                // Y pixels per meter
                0x6d, 0x0b, 0x00, 0x00,
                // Colors
                0x02, 0x00, 0x00, 0x00,
                // Important colors
                0x02, 0x00, 0x00, 0x00,
                // RGB quad of color 0   RGB quad of color 1
                (byte)0xff, (byte)0xff, (byte)0xff, 0x00, 0x00, 0x00, 0x00, 0x00
        };

        int linePad;
        if (width % 32 == 0) {
            linePad = 0;
        } else if (width % 32 <= 8) {
            linePad = 3;
        } else if (width % 32 <= 16) {
            linePad = 2;
        } else if (width % 32 <= 24) {
            linePad = 1;
        } else {
            linePad = 0;
        }

        int dataSize = height * (width / 2 + linePad);
        int fileSize = dataSize + BMP_PREAMBLE_LENGTH;

        byte[] bmp = new byte[fileSize];
        System.arraycopy(bmpPreamble, 0, bmp, 0, BMP_PREAMBLE_LENGTH);
        //
        bmp[2] = (byte) (fileSize & 0xff);
        bmp[3] = (byte) ((byte) (fileSize >> 8) & 0xff);
        bmp[4] = (byte) ((byte) (fileSize >> 16) & 0xff);
        bmp[5] = (byte) ((byte) (fileSize >> 24) & 0xff);

        bmp[18] = (byte) (width & 0xff);
        bmp[19] = (byte) ((byte) (width >> 8) & 0xff);
        bmp[20] = (byte) ((byte) (width >> 16) & 0xff);
        bmp[21] = (byte) ((byte) (width >> 24) & 0xff);

        bmp[22] = (byte) (height & 0xff);
        bmp[23] = (byte) ((height >> 8) & 0xff);
        bmp[24] = (byte) ((height >> 16) & 0xff);
        bmp[25] = (byte) ((height >> 24) & 0xff);

        bmp[34] = (byte)(dataSize & 0xff);
        bmp[35] = (byte)((dataSize >> 8) & 0xff);
        bmp[36] = (byte)((dataSize >> 16) & 0xff);
        bmp[37] = (byte)((dataSize >> 24) & 0xff);

        int bitmapLineLength = (width + 7) / 8;

        int i = height -1;
        int k = BMP_PREAMBLE_LENGTH;
        while (i >= 0) {
            System.arraycopy(data, bitmapLineLength * i, bmp, k, bitmapLineLength);
            i--;
            k += bitmapLineLength;
            for (int j = 0; j < linePad; j++, k++) {
                bmp[k]  = 0x00;
            }
        }
        return bmp;
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
    protected static String convertImage2Base64(final byte[] data, final int width, final int height) throws IOException {
        byte[] bytes;
        Base64.Encoder base64Encoder = Base64.getEncoder();
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            final BufferedImage res = ImageIO.read(new ByteArrayInputStream(bitmap2BMP(data, width, height)));
            ImageIO.write(res, "png", baos);
            baos.flush();
            bytes = baos.toByteArray();
        }
        return base64Encoder.encodeToString(bytes);
    }
}
