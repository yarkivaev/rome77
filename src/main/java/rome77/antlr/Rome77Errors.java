package rome77.antlr;

import java.util.Optional;
import lexical.LexicalException;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import syntax.SyntaxException;

/**
 * ANTLR error listener that captures first syntax or lexical error.
 *
 * This final class implements ANTLRErrorListener to capture
 * the first error as either SyntaxException or LexicalException.
 *
 * Example usage:
 * <pre>
 * Rome77Errors errors = new Rome77Errors();
 * parser.addErrorListener(errors);
 * parser.program();
 * Optional&lt;SyntaxException&gt; error = errors.captured();
 * </pre>
 */
public final class Rome77Errors extends BaseErrorListener {

    private SyntaxException error;
    private LexicalException lexerror;

    @Override
    public void syntaxError(
        final Recognizer<?, ?> recognizer,
        final Object symbol,
        final int ln,
        final int col,
        final String msg,
        final RecognitionException exc
    ) {
        if (recognizer instanceof org.antlr.v4.runtime.Lexer) {
            if (this.lexerror == null) {
                this.lexerror = new LexicalException(ln, col, msg);
            }
        } else {
            if (this.error == null) {
                this.error = new SyntaxException(ln, col, msg);
            }
        }
    }

    /**
     * Returns the first captured syntax error if any.
     *
     * @return Optional containing first syntax error, or empty if no errors
     */
    public Optional<SyntaxException> captured() {
        return Optional.ofNullable(this.error);
    }

    /**
     * Returns the first captured lexical error if any.
     *
     * @return Optional containing first lexical error, or empty if no errors
     */
    public Optional<LexicalException> lexical() {
        return Optional.ofNullable(this.lexerror);
    }
}
