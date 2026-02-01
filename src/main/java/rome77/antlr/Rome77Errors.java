package rome77.antlr;

import java.util.Optional;
import org.antlr.v4.runtime.BaseErrorListener;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import rome77.ParsingError;
import rome77.SyntaxError;

/**
 * ANTLR error listener that captures first syntax error.
 *
 * This final class implements ANTLRErrorListener to capture
 * the first syntax error. The error is stored and can be
 * retrieved to construct Either.Left result.
 *
 * Example usage:
 * <pre>
 * Rome77Errors errors = new Rome77Errors();
 * parser.addErrorListener(errors);
 * parser.program();
 * Optional&lt;ParsingError&gt; error = errors.captured();
 * </pre>
 */
public final class Rome77Errors extends BaseErrorListener {

    /**
     * First captured error.
     */
    private ParsingError error;

    @Override
    public void syntaxError(
        final Recognizer<?, ?> recognizer,
        final Object symbol,
        final int ln,
        final int col,
        final String msg,
        final RecognitionException exc
    ) {
        if (this.error == null) {
            this.error = new SyntaxError(ln, col, msg);
        }
    }

    /**
     * Returns the first captured error if any.
     *
     * @return Optional containing first error, or empty if no errors
     */
    public Optional<ParsingError> captured() {
        return Optional.ofNullable(this.error);
    }
}
