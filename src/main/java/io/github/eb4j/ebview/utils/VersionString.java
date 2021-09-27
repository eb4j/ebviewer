package io.github.eb4j.ebview.utils;

import java.util.ResourceBundle;

public final class VersionString {

    private VersionString() {
    }

    /** Full version, e.g. "1.0.0-0-123456-SNAPSHOT" */
    public static final String VERSION;

    static {
        ResourceBundle b = ResourceBundle.getBundle("version");
        VERSION = b.getString("version");
    }
}
