package web;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * HTTP handler for serving static frontend files.
 *
 * Maps request URI paths to files via an Assets instance.
 * Returns 404 if the requested file does not exist.
 *
 * Example usage:
 * <pre>
 * server.createContext("/", new StaticExchange(
 *     new FileAssets(Path.of("frontend/src"))
 * ));
 * </pre>
 */
public final class StaticExchange implements HttpHandler {

    private final Assets assets;

    /**
     * Primary constructor.
     *
     * @param assets static file provider
     */
    public StaticExchange(final Assets assets) {
        this.assets = assets;
    }

    @Override
    public void handle(final HttpExchange exchange) throws IOException {
        final String path = exchange.getRequestURI().getPath();
        try (InputStream content = this.assets.content(path)) {
            final byte[] bytes = content.readAllBytes();
            exchange.getResponseHeaders().add(
                "Content-Type", this.assets.mime(path)
            );
            exchange.sendResponseHeaders(200, bytes.length);
            try (OutputStream out = exchange.getResponseBody()) {
                out.write(bytes);
            }
        } catch (final IOException exception) {
            final byte[] msg = "Not Found".getBytes();
            exchange.sendResponseHeaders(404, msg.length);
            try (OutputStream out = exchange.getResponseBody()) {
                out.write(msg);
            }
        }
    }
}
