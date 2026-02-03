package ir;

/**
 * Unary operation expression.
 *
 * Represents prefix operators with single operand.
 *
 * Example usage:
 * <pre>
 * UnaryOp op = (UnaryOp) expr;
 * Operator operator = op.operator();
 * Expression operand = op.operand();
 * </pre>
 */
public interface UnaryOp extends Expression {

    /**
     * Returns the operator.
     *
     * Only ADD and SUB are valid for unary operations.
     *
     * @return Operator type, never null
     */
    Operator operator();

    /**
     * Returns the operand.
     *
     * @return Operand expression, never null
     */
    Expression operand();
}
