package rome77.antlr;

import lexical.Token;
import org.antlr.v4.runtime.Parser;

/**
 * Token wrapper for ANTLR token.
 *
 * Adapts org.antlr.v4.runtime.Token to immutable lexical.Token interface.
 * Captures token state at construction time.
 *
 * Example usage:
 * <pre>
 * Token token = new AntlrToken(antlrToken, parser);
 * </pre>
 */
public final class AntlrToken implements Token {

    private final org.antlr.v4.runtime.Token token;
    private final Parser parser;

    /**
     * Primary constructor.
     *
     * @param tok ANTLR token to wrap
     * @param prs Parser for vocabulary access
     */
    public AntlrToken(
        final org.antlr.v4.runtime.Token tok,
        final Parser prs
    ) {
        this.token = tok;
        this.parser = prs;
    }

    @Override
    public lexical.TokenCategory category() {
        final String name = this.parser.getVocabulary()
            .getSymbolicName(this.token.getType());
        try {
            return lexical.TokenCategory.valueOf(name);
        } catch (final IllegalArgumentException ex) {
            throw new IllegalStateException(
                String.format(
                    "Unknown token type: %s at %d:%d",
                    name,
                    this.token.getLine(),
                    this.token.getCharPositionInLine()
                ),
                ex
            );
        }
    }

    @Override
    public String text() {
        return this.token.getText();
    }

    @Override
    public int line() {
        return this.token.getLine();
    }

    @Override
    public int column() {
        return this.token.getCharPositionInLine();
    }
}
