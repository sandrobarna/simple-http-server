package sandrobarna.webserver;

import java.util.Arrays;

/**
 * Lists supported/implemented HTTP request methods.
 */
public enum HttpRequestMethod {

    GET, HEAD;

    /**
     * Checks if a given HTTP request method is supported.
     * @param methodName HTTP request method name (e.g. HEAD, GET, ...)
     * @return True if method is supported, otherwise false.
     */
    public static boolean isSupported(String methodName) {

        return Arrays.stream(HttpRequestMethod.values()).anyMatch(x -> x.name().equals(methodName));
    }
}
