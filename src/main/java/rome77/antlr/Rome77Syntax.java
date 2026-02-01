package rome77.antlr;

import fp.Either;
import fp.Left;
import fp.Right;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import rome77.ParsingError;
import rome77.Rome77Lexer;
import rome77.Rome77Parser;
import rome77.Syntax;
import syntax.SyntaxTree;

/**
 * Rome77 syntax parser using ANTLR-generated lexer and parser.
 *
 * This final class implements Syntax interface by wrapping ANTLR
 * runtime components. It creates immutable syntax tree from source code.
 *
 * Example usage:
 * <pre>
 * Either&lt;ParsingError, SyntaxTree&gt; result = new Rome77Syntax("Grafo XIV").parsed();
 * </pre>
 */
public final class Rome77Syntax implements Syntax {

    /**
     * Source code to parse.
     */
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
    public Either<ParsingError, SyntaxTree> parsed() {
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
        final Either<ParsingError, SyntaxTree> result;
        if (errors.captured().isPresent()) {
            result = new Left<>(errors.captured().get());
        } else {
            result = new Right<>(new AntlrTree(program, parser));
        }
        return result;
    }
}
