package es.uco.ecoagent.server;

import org.junit.Test;

import java.io.InputStream;
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
}
