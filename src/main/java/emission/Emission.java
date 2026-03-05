package emission;

import java.io.InputStream;

/**
 * Immutable sequence of LLVM IR instructions with register allocation.
 *
 * Tracks a counter for naming temporary registers (%t0, %t1, ...).
 * Each append creates a new Emission with the instruction added and counter incremented.
 *
 * Example usage:
 * <pre>
 * Emission em = new SimpleEmission();
 * String reg = em.registered();
 * em = em.appended(reg + " = add i64 1, 2");
 * InputStream ir = em.result();
 * </pre>
 */
public interface Emission {

    /**
     * Returns the current register name based on internal counter.
     *
     * @return Register name like "%t0", "%t1", etc
     */
    String registered();

    /**
     * Returns new Emission with instruction added and counter incremented.
     *
     * @param instruction LLVM IR instruction to append
     * @return New immutable Emission
     */
    Emission appended(String instruction);

    /**
     * Returns the full LLVM IR as an InputStream.
     *
     * @return InputStream containing all instructions joined by newlines
     */
    InputStream result();
}
