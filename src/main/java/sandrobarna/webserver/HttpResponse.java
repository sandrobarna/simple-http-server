package sandrobarna.webserver;

import java.util.HashMap;
import java.util.Map;

/**
 * Encapsulates HTTP response message.
 */
public class HttpResponse {

    public static class Builder {

        String httpVersion;
        HttpStatusCode httpStatusCode;
        Map<String, String> responseHeaders = new HashMap<>();
        String messageBody;

        public Builder setHttpVersion(String httpVersion) {
            this.httpVersion = httpVersion;
            return this;
        }

        public Builder setHttpStatusCode(HttpStatusCode statusCode) {
            this.httpStatusCode = statusCode;
            return this;
        }

        public Builder setResponseHeader(String name, String value) {
            this.responseHeaders.put(name.toLowerCase(), value);
            return this;
        }

        public Builder setResponseBody(String messageBody) {
            this.messageBody = messageBody;
            return this;
        }

        public HttpResponse build() {

            HttpResponse response = new HttpResponse();

            response.httpVersion = this.httpVersion;
            response.httpStatusCode = this.httpStatusCode;
            response.responseHeaders = this.responseHeaders;
            response.messageBody = this.messageBody == null ? "" : this.messageBody;

            return response;
        }

    }

    final static String CRLF = "\r\n";

    String httpVersion;
    HttpStatusCode httpStatusCode;
    Map<String, String> responseHeaders = new HashMap<>();
    String messageBody = "";

    @Override
    public String toString() {

        StringBuilder sb = new StringBuilder();

        sb.append(this.httpVersion);
        sb.append(' ');
        sb.append(this.httpStatusCode);
        sb.append(CRLF);

        for (Map.Entry<String, String> header : this.responseHeaders.entrySet()) {

            sb.append(String.format("%s: %s", header.getKey(), header.getValue()));
            sb.append(CRLF);
        }

        sb.append(CRLF);

        sb.append(this.messageBody);

        return sb.toString();
    }
}
