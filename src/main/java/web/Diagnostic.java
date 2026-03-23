package web;

/**
 * Single compilation diagnostic with severity and source location.
 *
 * Maps compiler errors to the JSON diagnostic format expected by the IDE.
 *
 * Example usage:
 * <pre>
 * Diagnostic diag = new ParseDiagnostic(exception);
 * String sev = diag.severity();
 * </pre>
 */
public interface Diagnostic {

    /**
     * Returns the severity level.
     *
     * @return "error" or "warning"
     */
    String severity();

    /**
     * Returns the 1-based starting line number.
     *
     * @return line number, always positive
     */
    int line();

    /**
     * Returns the 1-based starting column number.
     *
     * @return column number, always positive
     */
    int column();

    /**
     * Returns the diagnostic message.
     *
     * @return human-readable error description
     */
    String message();
}
