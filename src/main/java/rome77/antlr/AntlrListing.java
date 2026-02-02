package rome77.antlr;

import lexical.Listing;
import lexical.Token;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Parser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Token listing from ANTLR token stream.
 *
 * Wraps CommonTokenStream to provide immutable lexical.Listing interface.
 * Captures all tokens at construction time.
 *
 * Example usage:
 * <pre>
 * Listing listing = new AntlrListing(tokenStream, parser);
 * </pre>
 */
public final class AntlrListing implements Listing {

    private final List<Token> tokens;

    /**
     * Primary constructor.
     *
     * @param stream Token stream from lexer
     * @param parser Parser for vocabulary access
     */
    public AntlrListing(
        final CommonTokenStream stream,
        final Parser parser
    ) {
        stream.fill();
        final List<org.antlr.v4.runtime.Token> antlr = stream.getTokens();
        final List<Token> captured = new ArrayList<>(antlr.size());
        for (final org.antlr.v4.runtime.Token tok : antlr) {
            captured.add(new AntlrToken(tok, parser));
        }
        this.tokens = Collections.unmodifiableList(captured);
    }

    @Override
    public Iterable<Token> tokens() {
        return this.tokens;
    }

    @Override
    public int size() {
        return this.tokens.size();
    }
}
