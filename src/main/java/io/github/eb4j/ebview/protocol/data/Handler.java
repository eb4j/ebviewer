package io.github.eb4j.ebview.protocol.data;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

/**
 * @author Hiroshi Miura
 */
public class Handler extends URLStreamHandler {
    @Override
    protected URLConnection openConnection(final URL u) throws MalformedURLException {
        return new DataConnection(u);
    }
}
