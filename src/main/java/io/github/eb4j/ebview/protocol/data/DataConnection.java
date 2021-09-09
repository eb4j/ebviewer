package io.github.eb4j.ebview.protocol.data;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Base64;

public class DataConnection extends URLConnection {

    public DataConnection(final URL u) {
        super(u);
    }

    @Override
    public void connect() {
        connected = true;
    }

    @Override
    public InputStream getInputStream() {
        return new ByteArrayInputStream(getData());
    }

    @Override
    public String getContentType() {
        String[] headers = getHeader();
        if (headers.length > 0) {
            return headers[0];
        }
        return  null;
    }

    private byte[] getData() {
        String data = url.toString();
        data = data.replaceFirst("^.*;base64,", "");
        Base64.Decoder decoder = Base64.getDecoder();
        return decoder.decode(data);
    }

    private String[] getHeader() {
        String urlString = url.toString();
        int endOfHeader = urlString.indexOf(",");
        int endOfProtocol = urlString.indexOf(":");
        String[] header = urlString.substring(endOfProtocol + 1, endOfHeader).split(":");
        return header;
    }
}
