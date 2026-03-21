package parsing;

/**
 * Abstract checked exception for parsing errors.
 *
 * Provides location and message for diagnostics.
 * Subclasses represent specific types of parsing failures.
 *
 * Example usage:
 * <pre>
 * try {
 *     SyntaxTree tree = syntax.parsed();
 * } catch (ParsingException ex) {
 *     System.err.printf("Error at %d:%d: %s%n",
 *         ex.line(), ex.column(), ex.getMessage());
 * }
 * </pre>
 */
public abstract class ParsingException extends Exception {

    /**
     * Primary constructor.
     *
     * @param msg Error message describing what went wrong
     */
    public ParsingException(final String msg) {
        super(msg);
    }

    /**
     * Returns the line number where the error occurred.
     *
     * Line numbers are 1-based, matching typical editor display.
     *
     * @return Line number, 1-based, always positive
     */
    public abstract int line();

    /**
     * Returns the column position where the error occurred.
     *
     * Column positions are 0-based character offsets within the line.
     *
     * @return Column position, 0-based, non-negative
     */
    public abstract int column();
}
