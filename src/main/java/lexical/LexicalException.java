package lexical;

import parsing.ParsingException;

/**
 * Checked exception thrown when lexical analysis fails.
 *
 * Extends ParsingException with lexical error details.
 * Thrown when source contains invalid tokens.
 *
 * Example usage:
 * <pre>
 * try {
 *     Listing listing = lexer.tokenized();
 * } catch (LexicalException ex) {
 *     System.err.printf("Lexical error at %d:%d: %s%n",
 *         ex.line(), ex.column(), ex.getMessage());
 * }
 * </pre>
 */
public final class LexicalException extends ParsingException {

    private final int ln;
    private final int col;

    /**
     * Primary constructor.
     *
     * @param line Line number where error occurred
     * @param column Column position where error occurred
     * @param msg Error message
     */
    public LexicalException(final int line, final int column, final String msg) {
        super(msg);
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
