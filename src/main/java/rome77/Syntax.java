package rome77;

import fp.Either;
import syntax.SyntaxTree;

/**
 * Parses Rome77 source code into syntax tree.
 *
 * This interface represents the entry point for parsing Rome77 programs.
 * Implementations wrap ANTLR-generated parsers and produce immutable syntax tree.
 * Returns Either to represent success or failure without exceptions.
 *
 * Example usage:
 * <pre>
 * Syntax syntax = new Rome77Syntax("As x = V");
 * Either&lt;ParsingError, SyntaxTree&gt; result = syntax.parsed();
 * </pre>
 */
public interface Syntax {

    /**
     * Parses the source code and returns the result.
     *
     * This method performs lexical and syntactic analysis of Rome77 source.
     * Returns Either.Right with immutable SyntaxTree on success, or Either.Left
     * with ParsingError on failure. Never throws exceptions.
     *
     * @return Either containing ParsingError (left) or SyntaxTree (right)
     */
    Either<ParsingError, SyntaxTree> parsed();
}
