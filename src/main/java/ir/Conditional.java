package ir;

/**
 * Conditional expression.
 *
 * Represents if-then-else expression (Sinon construct).
 * Condition is zero-check: zero is false, non-zero is true.
 *
 * Example usage:
 * <pre>
 * Conditional cond = (Conditional) expr;
 * Expression condition = cond.condition();
 * Expression thenBranch = cond.thenBranch();
 * Expression elseBranch = cond.elseBranch();
 * </pre>
 */
public interface Conditional extends Expression {

    /**
     * Returns the condition expression.
     *
     * Evaluated first to determine which branch to take.
     * Zero means false, non-zero means true.
     *
     * @return Condition to evaluate, never null
     */
    Expression condition();

    /**
     * Returns the then-branch expression.
     *
     * Evaluated when condition is non-zero (true).
     *
     * @return Then expression, never null
     */
    Expression thenBranch();

    /**
     * Returns the else-branch expression.
     *
     * Evaluated when condition is zero (false).
     *
     * @return Else expression, never null
     */
    Expression elseBranch();
}
