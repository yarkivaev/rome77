package web;

/**
 * Parsed compile request containing Rome77 source code.
 *
 * Implementations extract source from different wire formats.
 *
 * Example usage:
 * <pre>
 * Request request = new GsonRequest(inputStream);
 * String source = request.source();
 * String input = request.input();
 * </pre>
 */
public interface Request {

    /**
     * Returns the Rome77 source code from the request.
     *
     * @return source code string
     */
    String source();

    /**
     * Returns the stdin payload for program execution.
     *
     * @return stdin input string
     */
    String input();
}
