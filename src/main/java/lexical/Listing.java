package lexical;

/**
 * Immutable sequence of lexical tokens.
 *
 * Provides iteration over tokens produced by lexical analysis.
 * Listings are immutable once created and safe for concurrent access.
 *
 * Example usage:
 * <pre>
 * Listing listing = lexer.tokenized();
 * for (Token token : listing.tokens()) {
 *     System.out.printf("%s: %s%n", token.category(), token.text());
 * }
 * </pre>
 */
public interface Listing {

    /**
     * Returns the tokens in this listing.
     *
     * Tokens are ordered as they appear in source code.
     * Includes EOF token at the end.
     * Never returns null, may be empty for empty source.
     *
     * @return Iterable of tokens, never null
     */
    Iterable<Token> tokens();

    /**
     * Returns the number of tokens in this listing.
     *
     * Counts all tokens including EOF if present.
     * Returns one for empty source (EOF only).
     *
     * @return Token count, non-negative
     */
    int size();
}
