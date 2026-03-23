package web;

import java.nio.file.Path;
import java.util.List;

/**
 * Endpoint implementation for lli execution.
 */
public final class LliEndpoint implements Endpoint {

    private final List<Path> candidates;
    private final int timeout;

    /**
     * Primary constructor.
     *
     * @param candidates ordered list of candidate lli paths
     * @param timeout execution timeout in seconds
     */
    public LliEndpoint(final List<Path> candidates, final int timeout) {
        this.candidates = List.copyOf(candidates);
        this.timeout = timeout;
    }

    @Override
    public List<Path> candidates() {
        return this.candidates;
    }

    @Override
    public int timeout() {
        return this.timeout;
    }
}
