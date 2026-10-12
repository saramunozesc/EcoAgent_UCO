package es.uco.ecoagent.tools.command;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Copies process output so commands cannot block on full buffers.
 */
final class StreamCollector implements Runnable {
    private final InputStream input;
    private final OutputStream output;

    /**
     * Creates a collector from an input stream to an output buffer.
     */
    StreamCollector(InputStream input, OutputStream output) {
        this.input = input;
        this.output = output;
    }

    /**
     * Drains bytes until the process stream is closed.
     */
    public void run() {
        byte[] buffer = new byte[4096];
        int read;
        try {
            while ((read = input.read(buffer)) != -1) {
                output.write(buffer, 0, read);
            }
        } catch (IOException ignored) {
            // Process termination can close the stream while collecting output.
        }
    }
}

