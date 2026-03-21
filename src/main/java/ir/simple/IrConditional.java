package ir.simple;

import emission.Emission;
import emission.Emitted;
import emission.simple.SimpleEmitted;
import ir.Conditional;
import ir.Expression;
import java.util.Objects;

/**
 * Conditional expression in intermediate representation.
 *
 * Immutable value object representing if-then-else expression.
 * Condition is zero-check: zero is false, non-zero is true.
 *
 * Example usage:
 * <pre>
 * Expression cond = new IrVariable("x");
 * Expression then = new IrLiteral(1);
 * Expression els = new IrLiteral(0);
 * Conditional ifExpr = new IrConditional(cond, then, els);
 * </pre>
 */
public final class IrConditional implements Conditional {

    private final Expression cond;
    private final Expression thenExpr;
    private final Expression elseExpr;

    /**
     * Primary constructor.
     *
     * @param condition Condition expression
     * @param thenBranch Then-branch expression
     * @param elseBranch Else-branch expression
     */
    public IrConditional(
        final Expression condition,
        final Expression thenBranch,
        final Expression elseBranch
    ) {
        this.cond = condition;
        this.thenExpr = thenBranch;
        this.elseExpr = elseBranch;
    }

    @Override
    public Emitted emitted(final Emission emission) {
        final Emitted condition = this.cond.emitted(emission);
        Emission em = condition.emission();
        final String cmp = em.registered();
        em = em.appended(
            cmp + " = icmp ne i64 " + condition.register() + ", 0"
        );
        final String result = em.registered();
        em = em.appended(result + " = alloca i64");
        final String suffix = em.registered().substring(2);
        em = em.appended(
            "br i1 " + cmp
                + ", label %then_" + suffix
                + ", label %else_" + suffix
        );
        em = em.appended("then_" + suffix + ":");
        final Emitted thenResult = this.thenExpr.emitted(em);
        em = thenResult.emission();
        em = em.appended(
            "store i64 " + thenResult.register() + ", ptr " + result
        );
        em = em.appended("br label %merge_" + suffix);
        em = em.appended("else_" + suffix + ":");
        final Emitted elseResult = this.elseExpr.emitted(em);
        em = elseResult.emission();
        em = em.appended(
            "store i64 " + elseResult.register() + ", ptr " + result
        );
        em = em.appended("br label %merge_" + suffix);
        em = em.appended("merge_" + suffix + ":");
        final String loaded = em.registered();
        return new SimpleEmitted(
            em.appended(loaded + " = load i64, ptr " + result),
            loaded
        );
    }

    /**
     * Returns the condition expression.
     *
     * @return Condition to evaluate
     */
    @Override
    public Expression condition() {
        return this.cond;
    }

    /**
     * Returns the then-branch expression.
     *
     * @return Then expression
     */
    @Override
    public Expression thenBranch() {
        return this.thenExpr;
    }

    /**
     * Returns the else-branch expression.
     *
     * @return Else expression
     */
    @Override
    public Expression elseBranch() {
        return this.elseExpr;
    }

    /**
     * Checks equality based on all branches.
     *
     * @param other Object to compare
     * @return True if other is Conditional with same structure
     */
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Conditional)) {
            return false;
        }
        final Conditional that = (Conditional) other;
        return this.condition().equals(that.condition()) &&
            this.thenBranch().equals(that.thenBranch()) &&
            this.elseBranch().equals(that.elseBranch());
    }

    /**
     * Returns hash code based on all branches.
     *
     * @return Hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(
            this.condition(),
            this.thenBranch(),
            this.elseBranch()
        );
    }
}
