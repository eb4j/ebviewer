package io.github.eb4j.ebview.protocol.data;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.net.URLCodec;
import org.apache.commons.io.Charsets;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * The data protocol connection driver.
 * <p>
 * Data protocol Syntax
 *      data:[<mediatype>][;base64],<data>
 * inspired from StackOverflow.
 *
 * @author Hiroshi Miura
 * @see <a href="https://stackoverflow.com/questions/9388264/jeditorpane-with-inline-image/9388757#9388757">
 *     JEditorPane with inline image</a>
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/HTTP/Basics_of_HTTP/Data_URIs">Data URLs</a>
 */
public class DataConnection extends URLConnection {

    private final static String DATA_PROTO_RE =  "data:((.*?/.*?)?(?:;(.*?)=(.*?))?)(?:;(base64)?)?,(.*)";
    private final static String DEFAULT_MIME_TYPE = "text/plain;charset=US-ASCII";

    private final Pattern re;
    private Matcher m;

    public DataConnection(final URL u) {
        super(u);
        re = Pattern.compile(DATA_PROTO_RE);
    }

    @Override
    public void connect() {
        String data = url.toString();
        m = re.matcher(data);
        connected = m.matches();
    }

    @Override
    public InputStream getInputStream() {
        return new ByteArrayInputStream(getData());
    }

    /**
     * Returns the value of the content-type header field.
     * <p>
     * data protocol defined with optional content-type field.
     * If omitted, defaults to text/plain;charset=US-ASCII.
     * @return. the content type of the resource that the URL references.
     */
    @Override
    public String getContentType() {
        if (!connected) {
            return null;
        }
        String contentType = m.group(1);
        if (contentType != null && !contentType.isBlank()) {
            return contentType;
        }
        return DEFAULT_MIME_TYPE;
    }

    private byte[] getData() {
        String contentType = m.group(1);
        String type = m.group(2);
        String attribute = m.group(3);
        String value = m.group(4);
        String b64 = m.group(5);
        String data = m.group(6);
        if ((contentType == null || contentType.isBlank() || type.startsWith("text/"))
                && !("base64".equals(b64))) {
            try {
                if ("charset".equals(attribute)) {
                    URLCodec codec = new URLCodec(value);
                    Charset cs = Charsets.toCharset(value);
                    return codec.decode(data).getBytes(cs);
                } else {
                    URLCodec codec = new URLCodec("US-ASCII");
                    return codec.decode(data).getBytes(StandardCharsets.US_ASCII);
                }
            } catch (DecoderException e) {
                return new byte[0];
            }
        } else {
            if ("base64".equals(b64)) {
                return Base64.decodeBase64(data);
            }
        }
        return new byte[0];
    }
}
