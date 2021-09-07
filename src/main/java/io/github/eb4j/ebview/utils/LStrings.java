package io.github.eb4j.ebview.utils;

import java.util.ResourceBundle;

public final class LStrings {

    /** Just a version, e.g. "1.0.0" */
    public static final String VERSION;

    /** Update number, e.g. 2, for 1.6.0_02 */
    public static final String UPDATE;

    static {
        ResourceBundle b = ResourceBundle.getBundle("version");
        VERSION = b.getString("version");
        UPDATE = b.getString("update");
    }

    /** Resource bundle that contains all the strings */
    private static ResourceBundle bundle = ResourceBundle.getBundle("Bundle");

    /**
     * Returns resource bundle.
     */
    public static ResourceBundle getResourceBundle() {
        return bundle;
    }

    /** Returns a localized String for a key */
    public static String getString(String key) {
        return bundle.getString(key);
    }

}
