package io.github.eb4j.ebview.utils;

import java.util.ResourceBundle;

public final class LStrings {

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
