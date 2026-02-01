package syntax;

import parsing.ParsingException;

/**
 * Checked exception thrown when parsing fails due to syntax error.
 *
 * Extends ParsingException with concrete line and column values.
 * Thrown when source code contains invalid syntax.
 *
 * Example usage:
 * <pre>
 * try {
 *     SyntaxTree tree = syntax.parsed();
 * } catch (SyntaxException ex) {
 *     System.err.printf("Syntax error at %d:%d: %s%n",
 *         ex.line(), ex.column(), ex.getMessage());
 * }
 * </pre>
 */
public final class SyntaxException extends ParsingException {

    private final int line;

    private final int column;

    /**
     * Primary constructor.
     *
     * @param ln Line number, 1-based
     * @param col Column position, 0-based
     * @param msg Error message
     */
    public SyntaxException(final int ln, final int col, final String msg) {
        super(msg);
        this.line = ln;
        this.column = col;
    }

    @Override
    public int line() {
        return this.line;
    }

    @Override
    public int column() {
        return this.column;
    }
}
