package es.uco.ecoagent.server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

/**
 * Serves the ForgeIA visual interface from classpath static resources.
 */
public final class ForgeiaWebServer {
    private final HttpServer server;

    /**
     * Creates a web server bound to the requested port.
     */
    public ForgeiaWebServer(int port) throws IOException {
        this.server = HttpServer.create(new InetSocketAddress(port), 0);
        this.server.createContext("/", new StaticResourceHandler());
    }

    /**
     * Starts serving requests.
     */
    public void start() {
        server.start();
    }

    /**
     * Stops the server after a short graceful delay.
     */
    public void stop() {
        server.stop(1);
    }

    /**
     * Starts the UI using port 8080 or the first command argument.
     */
    public static void main(String[] args) throws IOException {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : 8080;
        ForgeiaWebServer webServer = new ForgeiaWebServer(port);
        webServer.start();
        System.out.println("ForgeIA UI listening on http://localhost:" + port);
    }

    private static final class StaticResourceHandler implements HttpHandler {
        /**
         * Handles one static resource request.
         */
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            if (path == null || path.equals("/") || path.trim().isEmpty()) {
                path = "/index.html";
            }
            byte[] content = loadResource("/static" + path);
            if (content == null) {
                send(exchange, 404, "text/plain; charset=utf-8", "Not found".getBytes(StandardCharsets.UTF_8));
                return;
            }
            send(exchange, 200, contentType(path), content);
        }

        /**
         * Loads a classpath resource into memory.
         */
        private static byte[] loadResource(String path) throws IOException {
            try (InputStream input = ForgeiaWebServer.class.getResourceAsStream(path)) {
                if (input == null) {
                    return null;
                }
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                byte[] buffer = new byte[4096];
                int read;
                while ((read = input.read(buffer)) >= 0) {
                    output.write(buffer, 0, read);
                }
                return output.toByteArray();
            }
        }

        /**
         * Sends one HTTP response with cache-free headers for local work.
         */
        private static void send(HttpExchange exchange, int status, String type, byte[] content) throws IOException {
            exchange.getResponseHeaders().set("Content-Type", type);
            exchange.getResponseHeaders().set("Cache-Control", "no-store");
            exchange.sendResponseHeaders(status, content.length);
            try (OutputStream response = exchange.getResponseBody()) {
                response.write(content);
            }
        }

        /**
         * Returns a simple content type for known UI assets.
         */
        private static String contentType(String path) {
            if (path.endsWith(".css")) {
                return "text/css; charset=utf-8";
            }
            if (path.endsWith(".js")) {
                return "application/javascript; charset=utf-8";
            }
            if (path.endsWith(".svg")) {
                return "image/svg+xml; charset=utf-8";
            }
            return "text/html; charset=utf-8";
        }
    }
}
