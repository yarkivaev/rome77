package semantic;

import ir.Expression;
import ir.simple.IrVariable;
import syntax.SyntaxNode;

/**
 * Analyzes a variable syntax node into an IR variable reference.
 *
 * Validates the variable is defined in the current scope.
 * Produces a helpful error if the name is actually a function.
 *
 * Example usage:
 * <pre>
 * Expression var = new VariableExpression(node, sigs, scope).expression();
 * </pre>
 */
public final class VariableExpression implements NodeExpression {

    private final SyntaxNode node;
    private final Signatures sigs;
    private final Scope scope;

    /**
     * Primary constructor.
     *
     * @param node variable syntax node
     * @param sigs function signatures
     * @param scope current variable scope
     */
    public VariableExpression(
        final SyntaxNode node,
        final Signatures sigs,
        final Scope scope
    ) {
        this.node = node;
        this.sigs = sigs;
        this.scope = scope;
    }

    @Override
    public Expression expression() throws SemanticException {
        final String name = this.node.text();
        if (!this.scope.contains(name)) {
            if (this.sigs.contains(name)) {
                throw new SemanticException(
                    this.node.line(),
                    this.node.column(),
                    "Function " + name + " expects "
                        + this.sigs.arity(name, this.node)
                        + " arguments, got 0"
                );
            }
            throw new SemanticException(
                this.node.line(),
                this.node.column(),
                "Undefined variable: " + name
            );
        }
        return new IrVariable(name);
    }
}
