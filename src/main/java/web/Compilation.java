package web;

/**
 * Compiles Rome77 source code and produces a response.
 *
 * Runs the full compilation pipeline and catches exceptions
 * to map them into structured diagnostics.
 *
 * Example usage:
 * <pre>
 * Compilation compilation = new Rome77Compilation(
 *     source, runtime, candidates, 10
 * );
 * Response resp = compilation.response();
 * </pre>
 */
public interface Compilation {

    /**
     * Executes the compilation pipeline and returns a response.
     *
     * @return response containing output, error, or diagnostics
     */
    Response response();
}
