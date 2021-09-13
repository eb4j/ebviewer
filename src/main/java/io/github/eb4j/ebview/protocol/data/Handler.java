package io.github.eb4j.ebview.protocol.data;

import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

/**
 * @author Joop Eggen (SO with CC-BY-SA 4.0)
 * @author Hiroshi Miura
 */
public class Handler extends URLStreamHandler {
    // PKG should not have last `.data` for `data:` protocol.
    // final static String PKGNAME = Handler.class.getPackage().getName();
    // final static String PKG = PKGNAME.substring(0, PKGNAME.lastIndexOf('.'));
    // we use a static literal here.
    final static String PKG =  "io.github.eb4j.ebview.protocol";
    final static String KEY = "java.protocol.handler.pkgs";

    @Override
    protected URLConnection openConnection(final URL u) {
        return new DataConnection(u);
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
