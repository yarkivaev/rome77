package semantic;

import syntax.SyntaxNode;

/**
 * Function signature registry for arity validation.
 *
 * Maps function names to their parameter counts collected during pass one.
 * Used during pass two to validate function calls.
 *
 * Example usage:
 * <pre>
 * Signatures sigs = new SignatureCollection(root).signatures();
 * int count = sigs.arity("fib", node);
 * boolean exists = sigs.contains("fib");
 * </pre>
 */
public interface Signatures {

    /**
     * Returns the parameter count for a function.
     *
     * @param name function name
     * @param node syntax node for error location
     * @return parameter count
     * @throws SemanticException if the function is not defined
     */
    int arity(String name, SyntaxNode node) throws SemanticException;

    /**
     * Returns whether a function name is registered.
     *
     * @param name function name to check
     * @return true if the function exists
     */
    boolean contains(String name);
}
