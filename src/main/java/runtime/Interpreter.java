package runtime;

import java.nio.file.Path;

/**
 * Resolves the path to an LLVM IR interpreter executable.
 *
 * Searches known locations for the lli binary.
 *
 * Example usage:
 * <pre>
 * Path lli = new LliPath(candidates).resolved();
 * </pre>
 */
public interface Interpreter {

    /**
     * Resolves the path to the interpreter executable.
     *
     * @return Absolute path to the executable
     * @throws IllegalStateException if no executable found
     */
    Path resolved();
}
