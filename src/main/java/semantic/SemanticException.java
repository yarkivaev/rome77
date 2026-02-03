package semantic;

import parsing.ParsingException;

/**
 * Checked exception for semantic analysis errors.
 *
 * Reports semantic validation failures with source location.
 * Includes undefined symbols, duplicate declarations, and arity mismatches.
 *
 * Example usage:
 * <pre>
 * throw new SemanticException(
 *     line,
 *     column,
 *     "Undefined variable: x"
 * );
 * </pre>
 */
public final class SemanticException extends ParsingException {

    private final int ln;
    private final int col;

    /**
     * Primary constructor.
     *
     * @param line Line number, 1-based
     * @param column Column position, 0-based
     * @param message Error message
     */
    public SemanticException(
        final int line,
        final int column,
        final String message
    ) {
        super(message);
        this.ln = line;
        this.col = column;
    }

    @Override
    public int line() {
        return this.ln;
    }

    @Override
    public int column() {
        return this.col;
    }
}
