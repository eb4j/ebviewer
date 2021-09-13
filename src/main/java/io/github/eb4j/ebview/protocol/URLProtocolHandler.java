package io.github.eb4j.ebview.protocol;

public final class URLProtocolHandler {
    // PKG should not have last `.data` for `data:` protocol.
    private static final String PKG =  "io.github.eb4j.ebview.protocol";
    private static final String CONTENT_PATH_PROP = "java.protocol.handler.pkgs";

    private URLProtocolHandler() {
    }

    public static void install() {
        String handlerPkgs = System.getProperty(CONTENT_PATH_PROP, "");
        if (!handlerPkgs.contains(PKG)) {
            if (handlerPkgs.isEmpty()) {
                handlerPkgs = PKG;
            } else {
                handlerPkgs += "|" + PKG;
            }
            System.setProperty(CONTENT_PATH_PROP, handlerPkgs);
        }
    }
}
