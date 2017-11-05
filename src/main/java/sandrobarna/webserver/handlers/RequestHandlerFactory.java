package sandrobarna.webserver.handlers;

/**
 * Request handler factory.
 */
public class RequestHandlerFactory {

    /**
     * Gives default HTTP request handler implementation.
     * @return RequestHandler implementation.
     */
    public static RequestHandler getDefaultHandler() {
        return new FileRequestHandler();
    }
}
