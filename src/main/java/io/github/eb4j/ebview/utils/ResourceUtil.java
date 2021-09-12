package io.github.eb4j.ebview.utils;

import java.awt.Image;
import java.awt.Toolkit;
import java.net.URL;

public class ResourceUtil {

    private ResourceUtil() {
    }

    private static final String RESOURCES = "/";

    /**
     * @see <a href="http://iconhandbook.co.uk/reference/chart/">Icon Reference Chart</a>
     */
    public static final Image APP_ICON_32X32 = getBundledImage("EBViewer.png");
    public static final Image APP_ICON_16X16 = getBundledImage("EBViewer_small.png");
    public static final Image APP_ICON_SCALABLE = getBundledImage("EBViewer.svg");

    /**
     * Load icon.
     *
     * @param resourceName resource name
     * @return Image got from resource.
     */
    public static Image getImage(final String resourceName) {
        URL resourceURL = ResourceUtil.class.getResource(resourceName);
        return Toolkit.getDefaultToolkit().getImage(resourceURL);
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
