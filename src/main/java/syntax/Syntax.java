package syntax;

import parsing.ParsingException;

/**
 * Parses source code into syntax tree.
 *
 * This interface represents the entry point for parsing programs.
 * Implementations wrap parsers and produce immutable syntax tree.
 * Throws ParsingException on parsing failure.
 *
 * Example usage:
 * <pre>
 * Syntax syntax = new Rome77Syntax("As x = V");
 * SyntaxTree tree = syntax.parsed();
 * </pre>
 */
public interface Syntax {

    /**
     * Parses the source code and returns the syntax tree.
     *
     * This method performs lexical and syntactic analysis of source.
     * Returns immutable SyntaxTree on success, throws ParsingException on failure.
     *
     * @return Parsed syntax tree
     * @throws ParsingException if source contains parsing errors
     */
    SyntaxTree parsed() throws ParsingException;
}
