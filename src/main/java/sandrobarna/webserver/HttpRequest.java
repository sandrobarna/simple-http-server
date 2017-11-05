package sandrobarna.webserver;

import sandrobarna.webserver.exceptions.BadRequestException;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Encapsulates HTTP request message.
 */
public class HttpRequest {

    public static class Builder {

        static String getFirstNonEmptyLine(BufferedReader in) throws IOException {

            String line = in.readLine();
            while (line != null && line.length() == 0) {
                line = in.readLine();
            }

            if (line == null) {
                throw new IOException("Stream interrupted or empty.");
            }

            return line;
        }

        static void ensureHttpVersionIsValid(String httpVersion) throws BadRequestException {

            if (!httpVersion.matches("^HTTP/[0-9]\\.[0-9]$")) {
                throw new BadRequestException(HttpStatusCode.BAD_REQUST);
            }
        }

        /**
         * Reads input stream and tries to parse a valid HTTP request message.
         * @param in Input Stream
         * @return Parsed HTTP request object
         * @throws IOException
         * @throws BadRequestException
         */
        public static HttpRequest parseStream(BufferedReader in) throws IOException, BadRequestException {

            HttpRequest request = new HttpRequest();

            String line = getFirstNonEmptyLine(in);

            String[] startLine = line.split(" ", 3);
            if (startLine.length != 3) {
                throw new BadRequestException(HttpStatusCode.BAD_REQUST);
            }

            if (!HttpRequestMethod.isSupported(startLine[0])) {
                throw new BadRequestException(HttpStatusCode.NOT_IMPLEMENTED);
            }
            request.requestMethod = HttpRequestMethod.valueOf(startLine[0]);

            request.requestTarget = startLine[1];
            if (request.requestTarget.trim().length() == 0) {
                throw new BadRequestException(HttpStatusCode.BAD_REQUST);
            }

            request.httpVersion = startLine[2];
            ensureHttpVersionIsValid(startLine[2]);

            while ((line = in.readLine()) != null && line.length() > 0) {

                String header[] = line.split(":", 2);
                String fieldName = header[0].toLowerCase();

                // No whitespace is allowed between fieldName and colon.
                if (Character.isWhitespace(fieldName.charAt(fieldName.length() - 1))) {
                    throw new BadRequestException(HttpStatusCode.BAD_REQUST);
                }

                String fieldValue = header.length > 1 ? header[1].trim() : "";

                request.requestHeaders.put(fieldName, fieldValue);
            }

            if (line == null) {
                throw new IOException("Stream interrupted.");
            }

            return request;
        }
    }

    HttpRequestMethod requestMethod;
    String requestTarget;
    String httpVersion;
    Map<String, String> requestHeaders = new HashMap<>();

    HttpRequest() {}

    public HttpRequestMethod getRequestMethod() {
        return this.requestMethod;
    }

    public String getRequestTarget() {
        return this.requestTarget;
    }

    public String getHttpVersion() {
        return this.httpVersion;
    }

    public String getHeader(String fieldName) {
        return this.requestHeaders.getOrDefault(fieldName.toLowerCase(), "");
    }
}
