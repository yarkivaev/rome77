package ir;

/**
 * Binary operation expression.
 *
 * Represents arithmetic operations with two operands.
 *
 * Example usage:
 * <pre>
 * BinaryOp op = (BinaryOp) expr;
 * Operator operator = op.operator();
 * Expression left = op.left();
 * Expression right = op.right();
 * </pre>
 */
public interface BinaryOp extends Expression {

    /**
     * Returns the operator.
     *
     * @return Operator type, never null
     */
    Operator operator();

    /**
     * Returns the left operand.
     *
     * @return Left expression, never null
     */
    Expression left();

    /**
     * Returns the right operand.
     *
     * @return Right expression, never null
     */
    Expression right();
}
