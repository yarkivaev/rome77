package rome77.antlr;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import rome77.Rome77Lexer;
import rome77.Rome77Parser;
import parsing.ParsingException;
import syntax.Syntax;
import syntax.SyntaxException;
import syntax.SyntaxTree;

/**
 * Rome77 syntax parser using ANTLR-generated lexer and parser.
 *
 * This final class implements Syntax interface by wrapping ANTLR
 * runtime components. It creates immutable syntax tree from source code.
 *
 * Example usage:
 * <pre>
 * SyntaxTree tree = new Rome77Syntax("Grafo XIV").parsed();
 * </pre>
 */
public final class Rome77Syntax implements Syntax {

    private final String source;

    /**
     * Primary constructor.
     *
     * @param code Rome77 source code
     */
    public Rome77Syntax(final String code) {
        this.source = code;
    }

    @Override
    public SyntaxTree parsed() throws ParsingException {
        final Rome77Lexer lexer = new Rome77Lexer(
            CharStreams.fromString(this.source)
        );
        final Rome77Errors errors = new Rome77Errors();
        lexer.removeErrorListeners();
        lexer.addErrorListener(errors);
        final CommonTokenStream tokens = new CommonTokenStream(lexer);
        final Rome77Parser parser = new Rome77Parser(tokens);
        parser.removeErrorListeners();
        parser.addErrorListener(errors);
        final Rome77Parser.ProgramContext program = parser.program();
        if (errors.captured().isPresent()) {
            throw errors.captured().get();
        }
        return new AntlrTree(program, parser);
    }
}
