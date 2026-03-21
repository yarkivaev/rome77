package semantic;

import ir.Expression;
import ir.Operator;
import ir.simple.IrUnaryOp;
import syntax.SyntaxNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Analyzes a unaryOp syntax node into an IR unary operation.
 *
 * Children layout: [operator, operand].
 * Maps the operator token and recursively analyzes the operand.
 *
 * Example usage:
 * <pre>
 * Expression un = new UnaryExpression(node, sigs, scope).expression();
 * </pre>
 */
public final class UnaryExpression implements NodeExpression {

    private final SyntaxNode node;
    private final Signatures sigs;
    private final Scope scope;

    /**
     * Primary constructor.
     *
     * @param node unaryOp syntax node
     * @param sigs function signatures
     * @param scope current variable scope
     */
    public UnaryExpression(
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
        return new IrUnaryOp(
            this.operator(children.get(0)),
            new DispatchedExpression(
                children.get(1), this.sigs, this.scope
            ).expression()
        );
    }

    /**
     * Maps an operator token to an IR operator.
     *
     * @param token operator syntax node
     * @return IR operator
     */
    private Operator operator(
        final SyntaxNode token
    ) throws SemanticException {
        switch (token.name()) {
            case "PLUS": return Operator.ADD;
            case "MINUS": return Operator.SUB;
            default:
                throw new SemanticException(
                    token.line(),
                    token.column(),
                    "Undefined operator: " + token.text()
                );
        }
    }
}
