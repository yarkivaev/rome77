package lexical;

/**
 * Performs lexical analysis on source code.
 *
 * Implementations wrap lexers and produce immutable token listings.
 * Throws LexicalException on lexical errors.
 *
 * Example usage:
 * <pre>
 * Lexer lexer = new Rome77Lexer("As x = V");
 * Listing listing = lexer.tokenized();
 * </pre>
 */
public interface Lexer {

    /**
     * Tokenizes the source code and returns the token listing.
     *
     * This method performs lexical analysis of source.
     * Returns immutable Listing on success, throws LexicalException on failure.
     *
     * @return Token listing
     * @throws LexicalException if source contains lexical errors
     */
    Listing tokenized() throws LexicalException;
}
