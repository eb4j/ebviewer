package io.github.eb4j.ebview.utils;

import java.util.ResourceBundle;

public final class LStrings {

    private LStrings() {
    }

    /** Resource bundle that contains all the strings.
     */
    private static ResourceBundle bundle = ResourceBundle.getBundle("Bundle");

    /** Returns a localized String for a key.
     * @param key query string.
     * @return localized string.
     */
    public static String getString(final String key) {
        return bundle.getString(key);
    }

}
