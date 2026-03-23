package web;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.List;

/**
 * Entry point for the Rome77 HTTP compiler server.
 *
 * Creates an HTTP server serving the frontend IDE and compile API.
 * Loads the LLVM IR runtime from the classpath at startup.
 *
 * Example usage:
 * <pre>
 * new Main(8080).started();
 * </pre>
 */
public final class Main implements Server {

    private final int port;

    /**
     * Primary constructor.
     *
     * @param port TCP port to bind to
     */
    public Main(final int port) {
        this.port = port;
    }

    /**
     * JVM entry point.
     *
     * @param args command line arguments (unused)
     * @throws IOException if server fails to start
     */
    public static void main(final String... args) throws IOException {
        new Main(8080).started();
    }

    @Override
    public void started() throws IOException {
        final String runtime = new String(
            this.getClass()
                .getClassLoader()
                .getResourceAsStream("runtime.ll")
                .readAllBytes(),
            StandardCharsets.UTF_8
        );
        final List<Path> candidates = List.of(
            Path.of("/opt/homebrew/opt/llvm/bin/lli"),
            Path.of("/usr/local/opt/llvm/bin/lli"),
            Path.of("/usr/bin/lli")
        );
        final HttpServer server = HttpServer.create(
            new InetSocketAddress(this.port), 0
        );
        server.createContext(
            "/compile",
            new CompileExchange(runtime, candidates, 10)
        );
        server.createContext(
            "/",
            new StaticExchange(
                new FileAssets(Path.of("frontend/src"))
            )
        );
        server.start();
        System.out.println(
            "Server started on port " + this.port
        );
    }
}
