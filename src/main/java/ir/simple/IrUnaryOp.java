package ir.simple;

import ir.Expression;
import ir.Operator;
import ir.UnaryOp;
import java.util.Objects;

/**
 * Unary operation in intermediate representation.
 *
 * Immutable value object representing prefix operation with single operand.
 * Only ADD and SUB operators are valid for unary operations.
 *
 * Example usage:
 * <pre>
 * UnaryOp neg = new IrUnaryOp(Operator.SUB, new IrLiteral(5));
 * Operator op = neg.operator();
 * Expression operand = neg.operand();
 * </pre>
 */
public final class IrUnaryOp implements UnaryOp {

    private final Operator op;
    private final Expression expr;

    /**
     * Primary constructor.
     *
     * @param operator Operation type
     * @param operand Operand expression
     */
    public IrUnaryOp(final Operator operator, final Expression operand) {
        this.op = operator;
        this.expr = operand;
    }

    /**
     * Returns the operator.
     *
     * @return Operator type
     */
    @Override
    public Operator operator() {
        return this.op;
    }

    /**
     * Returns the operand.
     *
     * @return Operand expression
     */
    @Override
    public Expression operand() {
        return this.expr;
    }

    /**
     * Checks equality based on operator and operand.
     *
     * @param other Object to compare
     * @return True if other is UnaryOp with same operator and operand
     */
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof UnaryOp)) {
            return false;
        }
        final UnaryOp that = (UnaryOp) other;
        return this.operator() == that.operator() &&
            this.operand().equals(that.operand());
    }

    /**
     * Returns hash code based on operator and operand.
     *
     * @return Hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.operator(), this.operand());
    }
}
