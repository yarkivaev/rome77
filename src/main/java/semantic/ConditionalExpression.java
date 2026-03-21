package semantic;

import ir.Expression;
import ir.simple.IrConditional;
import syntax.SyntaxNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Analyzes a conditional (Sinon) syntax node into an IR conditional.
 *
 * Children layout: [SINON, condition, thenBranch, elseBranch].
 * Recursively analyzes all three sub-expressions.
 *
 * Example usage:
 * <pre>
 * Expression cond = new ConditionalExpression(node, sigs, scope).expression();
 * </pre>
 */
public final class ConditionalExpression implements NodeExpression {

    private final SyntaxNode node;
    private final Signatures sigs;
    private final Scope scope;

    /**
     * Primary constructor.
     *
     * @param node conditional syntax node
     * @param sigs function signatures
     * @param scope current variable scope
     */
    public ConditionalExpression(
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
        final List<SyntaxNode> children = new ArrayList<>();
        this.node.children().forEach(children::add);
        return new IrConditional(
            new DispatchedExpression(
                children.get(1), this.sigs, this.scope
            ).expression(),
            new DispatchedExpression(
                children.get(2), this.sigs, this.scope
            ).expression(),
            new DispatchedExpression(
                children.get(3), this.sigs, this.scope
            ).expression()
        );
    }
}
