package sandrobarna.webserver.exceptions;

import sandrobarna.webserver.HttpStatusCode;

/**
 * Thrown to indicate invalid HTTP request message format.
 */
public class BadRequestException extends Exception {

    HttpStatusCode errorCode;

    public BadRequestException(HttpStatusCode errorCode) {
        this.errorCode = errorCode;
    }
}