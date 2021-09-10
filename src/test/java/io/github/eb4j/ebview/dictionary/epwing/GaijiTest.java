package io.github.eb4j.ebview.dictionary.epwing;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class GaijiTest {

    @Test
    public void convertImageTest() throws IOException {
        byte[] data = new byte[] {0x00, 0x00, 0x00, 0x00, 0x00, (byte) 0xe0, 0x03, (byte) 0x80, 0x00,
                0x00, 0x00, 0x00, 0x03, (byte) 0xc0, 0x04, 0x20, 0x00, 0x20, 0x00,
                (byte) 0xe0, 0x03, 0x20, 0x04, 0x20, 0x04, 0x60, 0x03, (byte) 0xa0, 0x00, 0x00, 0x00, 0x00};
        String image = Utils.convertMonoGraphic2Base64(data, 16, 16);
        assertEquals("iVBORw0KGgoAAAANSUhEUgAAABAAAAAQAQMAAAAlPW0iAAAABlBMVEX///8AAABVwtN+AAAAKklEQVR4Xm"
                        + "NggIAHzMz8EBYj815GFhmmPwxMDAcYmWWZGIEMB6b/DhBZAHKDBT9OJvDqAAAAAElFTkSuQmCC", image);
    }
}
