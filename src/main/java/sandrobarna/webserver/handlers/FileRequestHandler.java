package sandrobarna.webserver.handlers;

import sandrobarna.webserver.Config;
import sandrobarna.webserver.HttpRequest;
import sandrobarna.webserver.HttpResponse;
import sandrobarna.webserver.HttpStatusCode;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * This class implements file-based request handler. That is, requests asking for file resource.
 * Currently supports HEAD and GET methods.
 */
public class FileRequestHandler implements RequestHandler {

    Path documentRoot;
    int maxURILength;
    int maxBodyLength;

    FileRequestHandler() {
        this.documentRoot = Paths.get(Config.INSTANCE.get("server.rootDir"));
        this.maxURILength = Integer.parseInt(Config.INSTANCE.get("http.maxURILength"));
        this.maxBodyLength = Integer.parseInt(Config.INSTANCE.get("http.maxBodyLength"));
    }

    void handleGETRequest(HttpRequest request, HttpResponse.Builder resBuilder, boolean includeBody) {

        resBuilder.setHttpStatusCode(HttpStatusCode.OK);

        String requestURI = request.getRequestTarget();

        if (requestURI.length() > this.maxURILength) {
            resBuilder.setHttpStatusCode(HttpStatusCode.URI_TOO_LONG);
            return;
        }

        if (requestURI.charAt(0) == '/') {
            requestURI = requestURI.substring(1);
        }

        Path filePath = documentRoot.resolve(requestURI).normalize();

        if (!filePath.startsWith(documentRoot)) {

            resBuilder.setHttpStatusCode(HttpStatusCode.FORBIDDEN);
            return;
        }

        if (!Files.exists(filePath)
                || !Files.isRegularFile(filePath, LinkOption.NOFOLLOW_LINKS)
                || !Files.isReadable(filePath)) {

            resBuilder.setHttpStatusCode(HttpStatusCode.NOT_FOUND);
            return;
        }

        if (!includeBody) {
            return;
        }

        File file = new File(filePath.toString());

        if (file.length() > 1024L * 1024L * this.maxBodyLength) {
            resBuilder.setHttpStatusCode(HttpStatusCode.TOO_LARGE);
            return;
        }

        try (FileInputStream fin = new FileInputStream(file)) {

            byte[] buff = new byte[(int) file.length()];
            fin.read(buff);

            resBuilder.setResponseBody(new String(buff, StandardCharsets.UTF_8));
            resBuilder.setResponseHeader("content-length", Long.toString(file.length()));

        } catch (IOException e) {
            resBuilder.setHttpStatusCode(HttpStatusCode.NOT_FOUND);
        }
    }

    @Override
    public void handle(HttpRequest request, HttpResponse.Builder resBuilder) {

        switch (request.getRequestMethod()) {
            case HEAD:
                handleGETRequest(request, resBuilder, false);
                break;
            case GET:
                handleGETRequest(request, resBuilder, true);
                break;
            default:
                resBuilder.setHttpStatusCode(HttpStatusCode.NOT_IMPLEMENTED);
        }
    }
}
