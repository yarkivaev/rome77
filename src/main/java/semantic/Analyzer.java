package semantic;

import ir.Program;
import parsing.ParsingException;

/**
 * Semantic analyzer for Rome77 programs.
 *
 * Analyzes syntax trees and produces intermediate representation.
 * Performs semantic validation including symbol resolution and arity checking.
 *
 * Example usage:
 * <pre>
 * SyntaxTree tree = syntax.parsed();
 * Program program = new Rome77Analyzer(tree).analyzed();
 * </pre>
 */
public interface Analyzer {

    /**
     * Analyzes syntax tree and returns IR program.
     *
     * Performs semantic analysis including:
     * - Symbol table construction
     * - Function and variable declaration tracking
     * - Reference resolution (declared before use)
     * - Function arity validation
     *
     * @return IR program representation
     * @throws ParsingException if semantic errors detected
     */
    Program analyzed() throws ParsingException;
}
