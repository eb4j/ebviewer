package io.github.eb4j.ebview.utils;

import java.util.ResourceBundle;

public class VersionString {

    /** Full version, e.g. "1.0.0-0-123456-SNAPSHOT" */
    public static final String VERSION;

    /** commit id */
    public static final String COMMIT;

    /** branch name */
    public static final String BRANCH;

    static {
        ResourceBundle b = ResourceBundle.getBundle("version");
        VERSION = b.getString("version");
        COMMIT = b.getString("commit");
        BRANCH = b.getString("branch");
    }

}
