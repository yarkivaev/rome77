package semantic;

import ir.Expression;
import ir.Operator;
import ir.simple.IrBinaryOp;
import ir.simple.IrInput;
import syntax.SyntaxNode;

import java.util.ArrayList;
import java.util.List;

/**
 * Central expression dispatcher for semantic analysis.
 *
 * Routes a syntax node to the appropriate {@link NodeExpression}
 * handler based on the node name. Handles unwrapping of intermediate
 * ANTLR grammar nodes introduced by operator precedence rules.
 *
 * Example usage:
 * <pre>
 * Expression ir = new DispatchedExpression(node, sigs, scope).expression();
 * </pre>
 */
public final class DispatchedExpression implements NodeExpression {

    private final SyntaxNode node;
    private final Signatures sigs;
    private final Scope scope;

    /**
     * Primary constructor.
     *
     * @param node expression syntax node
     * @param sigs function signatures
     * @param scope current variable scope
     */
    public DispatchedExpression(
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
        final SyntaxNode target = this.unwrapped();
        switch (target.name()) {
            case "conditional":
                return new ConditionalExpression(
                    target, this.sigs, this.scope
                ).expression();
            case "funcCall":
                return new CallExpression(
                    target, this.sigs, this.scope
                ).expression();
            case "addSub":
            case "mulDiv":
                return new BinaryExpression(
                    target, this.sigs, this.scope
                ).expression();
            case "funcArg":
            case "funcMult":
                return this.chained(target);
            case "unaryOp":
                return new UnaryExpression(
                    target, this.sigs, this.scope
                ).expression();
            case "romanLiteral":
                return new LiteralExpression(target).expression();
            case "variable":
                return new VariableExpression(
                    target, this.sigs, this.scope
                ).expression();
            case "readInput":
                return new IrInput();
            default:
                throw new SemanticException(
                    target.line(),
                    target.column(),
                    "Undefined expression: " + target.text()
                );
        }
    }

    /**
     * Builds a left-associative chain from a flat arithmetic node.
     *
     * Handles funcArg and funcMult nodes whose children alternate
     * between operands and operator tokens: [operand, op, operand, ...].
     *
     * @param target flat arithmetic node
     * @return IR expression chain
     * @throws SemanticException if semantic errors occur
     */
    private Expression chained(
        final SyntaxNode target
    ) throws SemanticException {
        final List<SyntaxNode> children = new ArrayList<>();
        target.children().forEach(children::add);
        Expression result = new DispatchedExpression(
            children.get(0), this.sigs, this.scope
        ).expression();
        for (int i = 1; i < children.size(); i += 2) {
            result = new IrBinaryOp(
                this.operator(children.get(i)),
                result,
                new DispatchedExpression(
                    children.get(i + 1), this.sigs, this.scope
                ).expression()
            );
        }
        return result;
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

    /**
     * Strips intermediate ANTLR wrapper nodes to reach the semantic node.
     *
     * Handles pass-through nodes like toMult, toUnary, toPrimary,
     * arithmetic, and parenthesized expressions.
     * The funcArg and funcMult rules are unlabeled: a single-child
     * node is a pass-through while multi-child is a flat arithmetic chain.
     *
     * @return unwrapped meaningful syntax node
     */
    private SyntaxNode unwrapped() {
        SyntaxNode current = this.node;
        while (true) {
            final String name = current.name();
            if (name.equals("toMult")
                || name.equals("toUnary")
                || name.equals("toPrimary")
                || name.equals("arithmetic")) {
                final List<SyntaxNode> children = new ArrayList<>();
                current.children().forEach(children::add);
                current = children.get(0);
                continue;
            }
            if (name.equals("funcArg") || name.equals("funcMult")) {
                final List<SyntaxNode> children = new ArrayList<>();
                current.children().forEach(children::add);
                if (children.size() == 1) {
                    current = children.get(0);
                    continue;
                }
                return current;
            }
            if (name.equals("parens")) {
                final List<SyntaxNode> children = new ArrayList<>();
                current.children().forEach(children::add);
                current = children.get(1);
                continue;
            }
            return current;
        }
    }
}
