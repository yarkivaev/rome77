package web;

import java.io.IOException;

/**
 * Startable HTTP server.
 *
 * Implementations bind to a port and begin serving requests.
 *
 * Example usage:
 * <pre>
 * new Main(8080).started();
 * </pre>
 */
public interface Server {

    /**
     * Starts the HTTP server and begins accepting requests.
     *
     * @throws IOException if the server cannot bind to the port
     */
    void started() throws IOException;
}
