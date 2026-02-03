package ir.simple;

import ir.Expression;
import ir.Output;

/**
 * Output statement in intermediate representation.
 *
 * Immutable value object representing Grafo statement that writes to stdout.
 *
 * Example usage:
 * <pre>
 * Expression expr = new IrLiteral(42);
 * Output out = new IrOutput(expr);
 * Expression value = out.expression();
 * </pre>
 */
public final class IrOutput implements Output {

    private final Expression expr;

    /**
     * Primary constructor.
     *
     * @param expression Expression to output
     */
    public IrOutput(final Expression expression) {
        this.expr = expression;
    }

    /**
     * Returns the expression to output.
     *
     * @return Expression to evaluate and print
     */
    @Override
    public Expression expression() {
        return this.expr;
    }

    /**
     * Checks equality based on expression.
     *
     * @param other Object to compare
     * @return True if other is Output with same expression
     */
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Output)) {
            return false;
        }
        final Output that = (Output) other;
        return this.expression().equals(that.expression());
    }

    /**
     * Returns hash code based on expression.
     *
     * @return Hash code
     */
    @Override
    public int hashCode() {
        return this.expression().hashCode();
    }
}
