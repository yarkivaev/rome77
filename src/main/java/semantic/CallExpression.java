package semantic;

import ir.Expression;
import ir.simple.IrCall;
import syntax.SyntaxNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Analyzes a funcCall syntax node into an IR function call.
 *
 * Children layout: [functionName, arg1, arg2, ...].
 * Validates function existence and correct arity.
 *
 * Example usage:
 * <pre>
 * Expression call = new CallExpression(node, sigs, scope).expression();
 * </pre>
 */
public final class CallExpression implements NodeExpression {

    private final SyntaxNode node;
    private final Signatures sigs;
    private final Scope scope;

    /**
     * Primary constructor.
     *
     * @param node funcCall syntax node
     * @param sigs function signatures
     * @param scope current variable scope
     */
    public CallExpression(
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
        final String name = children.get(0).text();
        final List<Expression> args = new ArrayList<>();
        for (int i = 1; i < children.size(); i++) {
            args.add(
                new DispatchedExpression(
                    children.get(i), this.sigs, this.scope
                ).expression()
            );
        }
        if (!this.sigs.contains(name)) {
            throw new SemanticException(
                this.node.line(),
                this.node.column(),
                "Undefined function: " + name
            );
        }
        final int expected = this.sigs.arity(name, this.node);
        if (expected != args.size()) {
            throw new SemanticException(
                this.node.line(),
                this.node.column(),
                "Function " + name + " expects "
                    + expected + " arguments, got " + args.size()
            );
        }
        return new IrCall(name, args);
    }
}
