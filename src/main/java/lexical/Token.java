package lexical;

/**
 * Immutable lexical token from source code.
 *
 * Each token represents a lexeme matched by the lexer.
 * Tokens are immutable and provide type, text, and location.
 *
 * Example usage:
 * <pre>
 * Token token = listing.tokens().iterator().next();
 * String name = token.category();
 * String value = token.text();
 * int location = token.line();
 * </pre>
 */
public interface Token {

    /**
     * Returns the token category.
     *
     * Returns the type of this token.
     * Never returns null.
     *
     * @return Token category, never null
     */
    TokenCategory category();

    /**
     * Returns the source text matched by this token.
     *
     * Returns exact matched text from source code.
     * Never returns null, may be empty for EOF.
     *
     * @return Matched source text, never null
     */
    String text();

    /**
     * Returns the source line number where this token starts.
     *
     * Line numbers are 1-based, matching typical editor display.
     *
     * @return Line number, 1-based, always positive
     */
    int line();

    /**
     * Returns the column position where this token starts.
     *
     * Column positions are 0-based character offsets within the line.
     *
     * @return Column position, 0-based, non-negative
     */
    int column();
}
