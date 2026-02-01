package rome77;

/**
 * Immutable implementation of ParsingError.
 *
 * This final class captures syntax error details including
 * source location and descriptive message.
 *
 * Example usage:
 * <pre>
 * ParsingError error = new SyntaxError(5, 10, "Unexpected token");
 * </pre>
 */
public final class SyntaxError implements ParsingError {

    /**
     * Line number where error occurred.
     */
    private final int row;

    /**
     * Column position where error occurred.
     */
    private final int col;

    /**
     * Error message.
     */
    private final String msg;

    /**
     * Primary constructor.
     *
     * @param ln Line number (1-based)
     * @param cl Column position (0-based)
     * @param text Error message
     */
    public SyntaxError(final int ln, final int cl, final String text) {
        this.row = ln;
        this.col = cl;
        this.msg = text;
    }

    @Override
    public int line() {
        return this.row;
    }

    @Override
    public int column() {
        return this.col;
    }

    @Override
    public String message() {
        return this.msg;
    }
}
