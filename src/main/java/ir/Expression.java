package ir;

import emission.Emission;
import emission.Emitted;

/**
 * Immutable intermediate representation expression.
 *
 * Base interface for all Rome77 expressions in IR form.
 * All expressions evaluate to integer values.
 *
 * Example usage:
 * <pre>
 * Expression expr = function.body();
 * Emitted result = expr.emitted(emission);
 * </pre>
 */
public interface Expression {

    /**
     * Emits LLVM IR for this expression.
     *
     * @param emission Current instruction sequence
     * @return Emitted result with updated emission and register name
     */
    Emitted emitted(Emission emission);
}
