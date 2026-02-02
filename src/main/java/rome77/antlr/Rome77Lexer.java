package rome77.antlr;

import lexical.Lexer;
import lexical.LexicalException;
import lexical.Listing;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import rome77.Rome77Parser;

/**
 * Rome77 lexer using ANTLR-generated lexer.
 *
 * This final class implements Lexer interface by wrapping ANTLR
 * runtime components. It creates immutable token listing from source code.
 *
 * Example usage:
 * <pre>
 * Listing listing = new Rome77Lexer("As x = V").tokenized();
 * </pre>
 */
public final class Rome77Lexer implements Lexer {

    private final String source;

    /**
     * Primary constructor.
     *
     * @param code Rome77 source code
     */
    public Rome77Lexer(final String code) {
        this.source = code;
    }

    @Override
    public Listing tokenized() throws LexicalException {
        final rome77.Rome77Lexer lexer = new rome77.Rome77Lexer(
            CharStreams.fromString(this.source)
        );
        final Rome77Errors errors = new Rome77Errors();
        lexer.removeErrorListeners();
        lexer.addErrorListener(errors);
        final CommonTokenStream tokens = new CommonTokenStream(lexer);
        tokens.fill();
        if (errors.lexical().isPresent()) {
            throw errors.lexical().get();
        }
        final Rome77Parser parser = new Rome77Parser(tokens);
        return new AntlrListing(tokens, parser);
    }
}
