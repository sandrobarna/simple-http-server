package sandrobarna.webserver;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import sandrobarna.webserver.HttpRequest;
import sandrobarna.webserver.HttpRequestMethod;
import sandrobarna.webserver.exceptions.BadRequestException;

import java.io.*;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class HttpRequestTest {

    public static class BuilderTest {

        BufferedReader stringToStream(String s) {
            InputStream is = new ByteArrayInputStream(s.getBytes(StandardCharsets.US_ASCII));
            return new BufferedReader(new InputStreamReader(is));
        }

        @Test
        public void correctlyParsesValidStartLine() throws Exception {

            String[] rawMessages = {
                    "GET / HTTP/1.1\r\n\r\n",
                    "GET /file.html HTTP/1.1\r\n\r\n",
                    "GET /folder/file.html HTTP/1.1\r\n\r\n",
                    "GET /folder/../fi_le3.js HTTP/1.0\r\n\r\n",
                    "HEAD /fol/der/.././//fi_le3.js HTTP/2.0\r\n\r\n",
            };

            for (String raw : rawMessages) {

                HttpRequest request = HttpRequest.Builder.parseStream(stringToStream(raw));

                assertEquals(String.format("%s %s %s",
                        request.getRequestMethod(),
                        request.getRequestTarget(),
                        request.getHttpVersion()), raw.substring(0, raw.length() - 4));
            }
        }

        @Test
        public void correctlyParsesValidStartLineWithPreceedingCRLF() throws Exception {

            String rawHttpMessage = "\r\n\r\nGET /index.html HTTP/1.0\r\n\r\n";

            HttpRequest request = HttpRequest.Builder.parseStream(stringToStream(rawHttpMessage));

            assertEquals(request.getRequestMethod(), HttpRequestMethod.GET);
            assertEquals(request.getRequestTarget(), "/index.html");
            assertEquals(request.getHttpVersion(), "HTTP/1.0");
        }

        @Test
        public void correctlyParsesValidMessageWithHeaders() throws Exception {

            String rawHttpMessage = "HEAD / HTTP/1.1\r\nHost: www.example.com:80  \r\nConnection:close\r\n\r\n";

            HttpRequest request = HttpRequest.Builder.parseStream(stringToStream(rawHttpMessage));

            assertEquals(request.getRequestMethod(), HttpRequestMethod.HEAD);
            assertEquals(request.getRequestTarget(), "/");
            assertEquals(request.getHttpVersion(), "HTTP/1.1");
            assertEquals(request.getHeader("Host"), "www.example.com:80");
            assertEquals(request.getHeader("Connection"), "close");
        }

        @Test
        public void correctlyHandlesInvalidStartLine() throws Exception {

            String[] rawMessages = {
                    "GAT / HTTP/1.1\r\n\r\n",
                    "GET  HTTP/1.1\r\n\r\n",
                    "GET   HTTP/1.1\r\n\r\n",
                    "GET / HTTA/1.1\r\n\r\n",
                    "GET / HTTP|1.1\r\n\r\n",
                    "GET /hello world.html HTTP/1.1\r\n\r\n",
                    "GET / HTTP/1.0",
                    "GET / HTTP/1.0\r\n",
                    "get / HTTP/1.1\r\n\r\n",
                    "GET / http/1.1\r\n\r\n",
                    " GET / HTTP/1.1\r\n\r\n",
            };

            for (String raw : rawMessages) {

                try {

                    HttpRequest.Builder.parseStream(stringToStream(raw));

                    fail();

                } catch (BadRequestException e) {
                } catch (IOException e) {}
            }
        }
    }
}
