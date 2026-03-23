package web;

import java.nio.file.Path;
import java.util.List;

/**
 * Execution endpoint configuration.
 *
 * Provides lli candidate locations and runtime timeout.
 */
public interface Endpoint {

    /**
     * Returns candidate lli binary paths.
     *
     * @return ordered candidate list
     */
    List<Path> candidates();

    /**
     * Returns execution timeout in seconds.
     *
     * @return timeout value
     */
    int timeout();
}
