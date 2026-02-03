package ir.simple;

import ir.BinaryOp;
import ir.Expression;
import ir.Operator;
import java.util.Objects;

/**
 * Binary operation in intermediate representation.
 *
 * Immutable value object representing arithmetic operation with two operands.
 *
 * Example usage:
 * <pre>
 * Expression left = new IrLiteral(3);
 * Expression right = new IrLiteral(5);
 * BinaryOp add = new IrBinaryOp(Operator.ADD, left, right);
 * Operator op = add.operator();
 * Expression l = add.left();
 * Expression r = add.right();
 * </pre>
 */
public final class IrBinaryOp implements BinaryOp {

    private final Operator op;
    private final Expression lft;
    private final Expression rgt;

    /**
     * Primary constructor.
     *
     * @param operator Operation type
     * @param left Left operand
     * @param right Right operand
     */
    public IrBinaryOp(
        final Operator operator,
        final Expression left,
        final Expression right
    ) {
        this.op = operator;
        this.lft = left;
        this.rgt = right;
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
     * Returns the left operand.
     *
     * @return Left expression
     */
    @Override
    public Expression left() {
        return this.lft;
    }

    /**
     * Returns the right operand.
     *
     * @return Right expression
     */
    @Override
    public Expression right() {
        return this.rgt;
    }

    /**
     * Checks equality based on operator and operands.
     *
     * @param other Object to compare
     * @return True if other is BinaryOp with same structure
     */
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof BinaryOp)) {
            return false;
        }
        final BinaryOp that = (BinaryOp) other;
        return this.operator() == that.operator() &&
            this.left().equals(that.left()) &&
            this.right().equals(that.right());
    }

    /**
     * Returns hash code based on operator and operands.
     *
     * @return Hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.operator(), this.left(), this.right());
    }
}
