package ir;

import emission.Emission;

/**
 * Statement in main body.
 *
 * Base interface for variable declarations and output statements.
 * All statements are immutable.
 *
 * Example usage:
 * <pre>
 * for (Statement stmt : program.statements()) {
 *     Emission em = stmt.emitted(emission);
 * }
 * </pre>
 */
public interface Statement {

    /**
     * Emits LLVM IR for this statement.
     *
     * @param emission Current instruction sequence
     * @return Updated emission with statement instructions appended
     */
    Emission emitted(Emission emission);
}
