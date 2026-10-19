package es.uco.ecoagent.server;

import org.junit.Test;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Verifies that the visual interface assets are packaged with the server.
 */
public final class ForgeiaWebServerTest {
    /**
     * Ensures the UI contains the required phase 8 screens.
     */
    @Test
    public void packagesMinimumVisualScreens() throws Exception {
        String html = read("/static/index.html");

        assertTrue(html.contains("Dashboard de proyectos"));
        assertTrue(html.contains("Crear proyecto desde una idea"));
        assertTrue(html.contains("Lista de requisitos"));
        assertTrue(html.contains("Context Slice"));
        assertTrue(html.contains("Vista diff antes/despues"));
        assertTrue(html.contains("Build y tests"));
        assertTrue(html.contains("Metricas de consumo"));
        assertTrue(html.contains("Anadir nuevo requisito"));
        assertTrue(html.contains("REQ-014 Sistema de logros"));
    }

    /**
     * Ensures styling and navigation scripts are available.
     */
    @Test
    public void packagesUiAssets() throws Exception {
        assertTrue(read("/static/styles.css").contains(".defense-layout"));
        assertTrue(read("/static/app.js").contains("showScreen"));
        assertTrue(read("/static/app.js").contains("/api/health"));
    }

    @Test
    public void exposesHealthAndDelegatesRunRequests() throws Exception {
        ForgeiaWebServer server = new ForgeiaWebServer(0,
                body -> "{\"accepted\":true,\"length\":" + body.length() + "}");
        server.start();
        try {
            HttpResult health = request(server.getPort(), "GET", "/api/health", null);
            assertTrue(health.status == 200);
            assertTrue(health.body.contains("\"status\":\"UP\""));

            HttpResult run = request(server.getPort(), "POST", "/api/runs", "{\"idea\":\"demo\"}");
            assertTrue(run.status == 200);
            assertTrue(run.body.contains("\"accepted\":true"));
        } finally {
            server.stop();
        }
    }

    /**
     * Reads one classpath resource as UTF-8 text.
     */
    private static String read(String resource) throws Exception {
        InputStream input = ForgeiaWebServerTest.class.getResourceAsStream(resource);
        assertNotNull(input);
        byte[] data = new byte[input.available()];
        int read = input.read(data);
        input.close();
        return new String(data, 0, read, StandardCharsets.UTF_8);
    }

    private static HttpResult request(int port, String method, String path, String body) throws Exception {
        HttpURLConnection connection = (HttpURLConnection) new URL(
                "http://127.0.0.1:" + port + path).openConnection();
        connection.setRequestMethod(method);
        connection.setConnectTimeout(3000);
        connection.setReadTimeout(3000);
        if (body != null) {
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");
            try (OutputStream output = connection.getOutputStream()) {
                output.write(body.getBytes(StandardCharsets.UTF_8));
            }
        }
        int status = connection.getResponseCode();
        InputStream input = status >= 400 ? connection.getErrorStream() : connection.getInputStream();
        byte[] data = new byte[input.available()];
        int length = input.read(data);
        input.close();
        connection.disconnect();
        return new HttpResult(status, new String(data, 0, length, StandardCharsets.UTF_8));
    }

    private static final class HttpResult {
        private final int status;
        private final String body;

        private HttpResult(int status, String body) {
            this.status = status;
            this.body = body;
        }
    }
}
