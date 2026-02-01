package rome77;

/**
 * Immutable parsing error with source location.
 *
 * This interface represents a syntax error encountered during parsing.
 * It provides location information for error reporting and diagnostics.
 *
 * Example usage:
 * <pre>
 * ParsingError error = result.left().get();
 * System.err.printf("Error at %d:%d: %s%n",
 *     error.line(), error.column(), error.message());
 * </pre>
 */
public interface ParsingError {

    /**
     * Returns the line number where the error occurred.
     *
     * Line numbers are 1-based, matching typical editor display.
     *
     * @return Line number, 1-based, always positive
     */
    int line();

    /**
     * Returns the column position where the error occurred.
     *
     * Column positions are 0-based character offsets within the line.
     *
     * @return Column position, 0-based, non-negative
     */
    int column();

    /**
     * Returns the error message describing what went wrong.
     *
     * The message is human-readable and describes the syntax error.
     *
     * @return Error message, never null or empty
     */
    String message();
}
