package io.github.eb4j.ebview.protocol.data;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DataConnectionTest {

    @BeforeEach
    public void init() {
        Handler.install();
    }

    @Test
    public void getContentTypeTest1() throws MalformedURLException {
        URL url = new URL("data:image/png;base64,12341234123412341234123412341234aabbccdd");
        DataConnection conn = new DataConnection(url);
        conn.connect();
        assertEquals("image/png", conn.getContentType());
    }

    @Test
    public void getContentTypeTest2() throws MalformedURLException {
        URL url = new URL("data:;base64,12341234123412341234123412341234aabbccdd");
        DataConnection conn = new DataConnection(url);
        conn.connect();
        assertEquals("text/plain;charset=US-ASCII", conn.getContentType());

    }

    @Test
    public void getContentTypeTest3() throws IOException {
        URL url = new URL("data:,Hello%2C%20World%21");
        DataConnection conn = new DataConnection(url);
        conn.connect();
        assertEquals("text/plain;charset=US-ASCII", conn.getContentType());
        assertEquals("Hello, World!", getContent(conn));
    }

    @Test
    public void getContentTypeTest4() throws IOException {
        URL url = new URL("data:text/plain;base64,SGVsbG8sIFdvcmxkIQ==");
        DataConnection conn = new DataConnection(url);
        conn.connect();
        assertEquals("text/plain", conn.getContentType());
        assertEquals("Hello, World!", getContent(conn));
    }

    @Test
    public void getContentTypeTest5() throws IOException {
        URL url = new URL("data:text/html,%3Ch1%3EHello%2C%20World%21%3C%2Fh1%3E");
        DataConnection conn = new DataConnection(url);
        conn.connect();
        assertEquals("text/html", conn.getContentType());
        assertEquals("<h1>Hello, World!</h1>", getContent(conn));
    }

    private String getContent(final DataConnection conn) throws IOException {
        return new BufferedReader(
                new InputStreamReader((InputStream) conn.getContent(), StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));
    }
}