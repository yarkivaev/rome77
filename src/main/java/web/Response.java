package web;

/**
 * Compilation response serializable to JSON.
 *
 * Contains output, error, and diagnostics fields matching
 * the IDE frontend expected API contract.
 *
 * Example usage:
 * <pre>
 * Response resp = new CompileResponse("42\n", "", List.of());
 * String json = resp.json();
 * </pre>
 */
public interface Response {

    /**
     * Returns this response as a JSON string.
     *
     * @return JSON string matching the frontend API contract
     */
    String json();
}
