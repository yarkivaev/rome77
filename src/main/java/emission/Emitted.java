package emission;

/**
 * Result of emitting one expression as LLVM IR.
 *
 * Pairs the updated instruction sequence with the register name
 * where the expression result lives.
 *
 * Example usage:
 * <pre>
 * Emitted result = expression.emitted(emission);
 * Emission updated = result.emission();
 * String reg = result.register();
 * </pre>
 */
public interface Emitted {

    /**
     * Returns the updated instruction sequence after emission.
     *
     * @return Updated Emission with new instructions appended
     */
    Emission emission();

    /**
     * Returns the register or value name where the result lives.
     *
     * @return Register name like "%t0" or inline constant like "5"
     */
    String register();
}
