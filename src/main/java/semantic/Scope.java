package semantic;

import syntax.SyntaxNode;

/**
 * Immutable variable scope for semantic analysis.
 *
 * Tracks which variable names are visible at a given point in the program.
 * Each addition returns a new scope; the original remains unchanged.
 *
 * Example usage:
 * <pre>
 * Scope empty = new AnalysisScope();
 * Scope extended = empty.with("x", node);
 * boolean visible = extended.contains("x");
 * </pre>
 */
public interface Scope {

    /**
     * Returns a new scope containing an additional variable.
     *
     * @param name variable name to add
     * @param node syntax node for error location
     * @return new scope with the variable added
     * @throws SemanticException if the variable is already defined
     */
    Scope with(String name, SyntaxNode node) throws SemanticException;

    /**
     * Returns whether a variable name is visible in this scope.
     *
     * @param name variable name to check
     * @return true if the name is in scope
     */
    boolean contains(String name);
}
