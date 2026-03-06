package runtime.simple;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import runtime.Interpreter;

/**
 * Finds the lli executable among a list of candidate paths.
 *
 * Iterates through candidates and returns the first executable one.
 *
 * Example usage:
 * <pre>
 * Path lli = new LliPath(
 *     List.of(Path.of("/opt/homebrew/opt/llvm/bin/lli"))
 * ).resolved();
 * </pre>
 */
public final class LliPath implements Interpreter {

    private final List<Path> candidates;

    /**
     * Primary constructor.
     *
     * @param candidates Ordered list of candidate paths to check
     */
    public LliPath(final List<Path> candidates) {
        this.candidates = List.copyOf(candidates);
    }

    @Override
    public Path resolved() {
        for (final Path candidate : this.candidates) {
            if (Files.isExecutable(candidate)) {
                return candidate;
            }
        }
        throw new IllegalStateException(
            "No lli executable found among candidates: " + this.candidates
        );
    }
}
