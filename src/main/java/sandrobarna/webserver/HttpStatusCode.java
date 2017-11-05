package sandrobarna.webserver;

/**
 * Lists subset of HTTP status codes used by current implementation of the server.
 */
public enum HttpStatusCode {

    OK(200, "OK"),
    BAD_REQUST(400, "Bad Request"),
    FORBIDDEN(403, "Forbidden"),
    NOT_FOUND(404, "Not Found"),
    TOO_LARGE(413, "Payload Too Large"),
    URI_TOO_LONG(414, "Request-URI Too Long"),
    NOT_IMPLEMENTED(501, "Not Implemented"),
    VERSION_NOT_SUPPORTED(505, "HTTP Version not supported");

    int errorCode;
    String description;

    HttpStatusCode(int errorCode, String description) {
        this.errorCode = errorCode;
        this.description = description;
    }

    public int getErrorCode(){
        return this.errorCode;
    }

    public String getDescription() {
        return this.description;
    }

    @Override
    public String toString() {
        return String.format("%d %s", this.errorCode, this.description);
    }
}