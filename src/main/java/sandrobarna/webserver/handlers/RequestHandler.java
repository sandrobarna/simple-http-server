package sandrobarna.webserver.handlers;

import sandrobarna.webserver.HttpRequest;
import sandrobarna.webserver.HttpResponse;

/**
 * Defines the interface for HTTP request handlers.
 */
public interface RequestHandler {

    /**
     * This method receives HTTP request object and returns builder object corresponding response.
     * @param request HTTP request object.
     * @param resBuilder Builder object for HTTP response.
     */
    public void handle(HttpRequest request, HttpResponse.Builder resBuilder);
}
