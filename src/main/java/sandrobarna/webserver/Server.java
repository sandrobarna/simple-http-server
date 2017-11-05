package sandrobarna.webserver;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sandrobarna.webserver.exceptions.BadRequestException;
import sandrobarna.webserver.handlers.RequestHandler;
import sandrobarna.webserver.handlers.RequestHandlerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * Initializes executor and dispatches incoming HTTP requests to worker threads.
 */
public class Server {

    static final Logger LOGGER = LogManager.getLogger(Server.class);

    static final String SUPORTED_HTTP_VERSION = "HTTP/1.1";

    int portNumber;
    int threadPoolSize;

    public Server() {
        this.portNumber = Integer.parseInt(Config.INSTANCE.get("server.port"));
        this.threadPoolSize = Integer.parseInt(Config.INSTANCE.get("server.threadPoolSize"));
    }

    String getCurrentDateTime() {

        return ZonedDateTime
                .now(ZoneOffset.UTC)
                .format(DateTimeFormatter.ofPattern("EEE, dd MMM yyyy HH:mm:ss O"));
    }

    void handleRequest(Socket clientSocket) {

        try (
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        ) {

            HttpRequest request = null;

            HttpResponse.Builder resBuilder = new HttpResponse
                    .Builder()
                    .setHttpVersion(SUPORTED_HTTP_VERSION)
                    .setResponseHeader("Date", this.getCurrentDateTime())
                    .setResponseHeader("Connection", "close");

            RequestHandler requestHandler = RequestHandlerFactory.getDefaultHandler();

            try {

                request = HttpRequest.Builder.parseStream(in);

                if (!request.getHttpVersion().equals(SUPORTED_HTTP_VERSION)) {

                    resBuilder.setHttpStatusCode(HttpStatusCode.VERSION_NOT_SUPPORTED);

                } else {

                    requestHandler.handle(request, resBuilder);
                }

            } catch (BadRequestException e) {

                resBuilder.setHttpStatusCode(HttpStatusCode.BAD_REQUST);
            }

            out.write(resBuilder.build().toString());
            out.flush();

        } catch (IOException e) {

            LOGGER.warn("IOException: " + e.getMessage());

        } finally {

            try {

                clientSocket.close();

            } catch (IOException e) {

                LOGGER.warn("IOException on closing the connection: " + e.getMessage());
            }
        }
    }

    /**
     * Starts the server by initializing executor and listener for incoming connections.
     * @throws IOException
     */
    public void start() throws IOException {

        System.out.println("Staring server...");

        try (ServerSocket serverSocket = new ServerSocket(this.portNumber)) {

            Executor threadPool = Executors.newFixedThreadPool(this.threadPoolSize);

            System.out.println("Listening to incoming connections on port: " + this.portNumber);

            while (true) {

                try {

                    Socket clientSocket = serverSocket.accept();

                    threadPool.execute(() -> handleRequest(clientSocket));

                    LOGGER.info("Incoming connection accepted: " + clientSocket.toString());

                } catch (IOException e) {

                    LOGGER.warn("Incoming connection failed: " + e.getMessage());
                }
            }
        }
    }

    public static void main(String[] args) {

        try {

            new Server().start();

        } catch (IOException e) {

            LOGGER.error(e.getMessage());
        }
    }
}
