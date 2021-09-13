package io.github.eb4j.ebview.protocol;

public final class URLProtocolHandler {
    // PKG should not have last `.data` for `data:` protocol.
    final static String PKG =  "io.github.eb4j.ebview.protocol";
    final static String KEY = "java.protocol.handler.pkgs";

    private URLProtocolHandler() {
    }

    public static void install() {
        String handlerPkgs = System.getProperty(KEY, "");
        if (!handlerPkgs.contains(PKG)) {
            if (handlerPkgs.isEmpty()) {
                handlerPkgs = PKG;
            } else {
                handlerPkgs += "|" + PKG;
            }
            System.setProperty(KEY, handlerPkgs);
        }
    }
}
