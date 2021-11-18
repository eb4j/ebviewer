package io.github.eb4j.ebview.utils;

import javax.imageio.ImageIO;
import java.awt.Image;
import java.io.IOException;
import java.net.URL;

public final class ResourceUtil {

    private ResourceUtil() {
    }

    private static final String RESOURCES = "/";

    /**
     * Application icon 32x32 size.
     * @see <a href="http://iconhandbook.co.uk/reference/chart/">Icon Reference Chart</a>
     */
    public static final Image APP_ICON_32X32 = getBundledImage("EBViewer.png");
    /**
     * Application icon 16x16 size.
     */
    public static final Image APP_ICON_16X16 = getBundledImage("EBViewer_small.png");
    /**
     * Application icon scalable.
     */
    public static final Image APP_ICON_SCALABLE = getBundledImage("EBViewer.svg");

    /**
     * Load icon.
     *
     * @param resourceName resource name
     * @return Image got from resource.
     */
    public static Image getImage(final String resourceName) {
        URL resourceURL = ResourceUtil.class.getResource(resourceName);
        if (resourceURL != null) {
            try {
                return ImageIO.read(resourceURL);
            } catch (IOException ignored) {
            }
        }
        return null;
    }

    /**
     * Load icon from classpath.
     *
     * @param imageName
     *            icon file name
     * @return icon instance
     */
    public static Image getBundledImage(final String imageName) {
        return getImage(RESOURCES + imageName);
    }

}
