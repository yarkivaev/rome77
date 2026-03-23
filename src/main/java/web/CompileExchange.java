package web;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;

/**
 * HTTP handler for POST /compile requests.
 *
 * Reads JSON request body, runs the Rome77 compilation pipeline,
 * and writes a JSON response back.
 *
 * Example usage:
 * <pre>
 * server.createContext("/compile", new CompileExchange(
 *     runtimeIr, candidates, 10
 * ));
 * </pre>
 */
public final class CompileExchange implements HttpHandler {

    private final String runtime;
    private final List<Path> candidates;
    private final int timeout;

    /**
     * Primary constructor.
     *
     * @param runtime LLVM IR runtime definitions
     * @param candidates ordered list of candidate lli paths
     * @param timeout execution timeout in seconds
     */
    public CompileExchange(
        final String runtime,
        final List<Path> candidates,
        final int timeout
    ) {
        this.runtime = runtime;
        this.candidates = List.copyOf(candidates);
        this.timeout = timeout;
    }

    @Override
    public void handle(final HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add(
            "Content-Type", "application/json"
        );
        final String source = new GsonRequest(
            exchange.getRequestBody()
        ).source();
        final String json = new Rome77Compilation(
            source, this.runtime, this.candidates, this.timeout
        ).response().json();
        final byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(200, bytes.length);
        try (OutputStream out = exchange.getResponseBody()) {
            out.write(bytes);
        }
    }
}
