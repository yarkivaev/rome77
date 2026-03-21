package semantic;

import ir.Expression;
import ir.Operator;
import ir.simple.IrBinaryOp;
import syntax.SyntaxNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Analyzes an addSub or mulDiv syntax node into an IR binary operation.
 *
 * Children layout: [left, operator, right].
 * Maps the operator token and recursively analyzes both operands.
 *
 * Example usage:
 * <pre>
 * Expression bin = new BinaryExpression(node, sigs, scope).expression();
 * </pre>
 */
public final class BinaryExpression implements NodeExpression {

    private final SyntaxNode node;
    private final Signatures sigs;
    private final Scope scope;

    /**
     * Primary constructor.
     *
     * @param node addSub or mulDiv syntax node
     * @param sigs function signatures
     * @param scope current variable scope
     */
    public BinaryExpression(
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
        return new IrBinaryOp(
            this.operator(children.get(1)),
            new DispatchedExpression(
                children.get(0), this.sigs, this.scope
            ).expression(),
            new DispatchedExpression(
                children.get(2), this.sigs, this.scope
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
            case "MULT": return Operator.MUL;
            case "DIV": return Operator.DIV;
            default:
                throw new SemanticException(
                    token.line(),
                    token.column(),
                    "Undefined operator: " + token.text()
                );
        }
    }
}
