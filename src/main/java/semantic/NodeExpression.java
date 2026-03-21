package semantic;

import ir.Expression;

/**
 * Produces an IR expression from a syntax node in a given context.
 *
 * Each implementation handles one category of syntax node
 * and recursively delegates sub-expressions via
 * {@link DispatchedExpression}.
 *
 * Example usage:
 * <pre>
 * Expression ir = new DispatchedExpression(node, sigs, scope).expression();
 * </pre>
 */
public interface NodeExpression {

    /**
     * Returns the IR expression for the encapsulated syntax node.
     *
     * @return IR expression
     * @throws SemanticException if semantic errors are found
     */
    Expression expression() throws SemanticException;
}
