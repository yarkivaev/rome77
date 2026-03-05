package ir.simple;

import emission.Emission;
import emission.Emitted;
import emission.simple.SimpleEmitted;
import ir.Input;

/**
 * Input expression in intermediate representation.
 *
 * Immutable value object representing Anagnosi expression that reads from stdin.
 * All input expressions are semantically equal as they perform the same operation.
 *
 * Example usage:
 * <pre>
 * Input in = new IrInput();
 * // No additional methods - input operation is stateless
 * </pre>
 */
public final class IrInput implements Input {

    /**
     * Primary constructor.
     */
    public IrInput() {
    }

    @Override
    public Emitted emitted(final Emission emission) {
        final String reg = emission.registered();
        return new SimpleEmitted(
            emission.appended(reg + " = call i64 @rome77_input()"),
            reg
        );
    }

    /**
     * Checks equality with other inputs.
     *
     * All Input instances are equal because they represent the same operation.
     *
     * @param other Object to compare
     * @return True if other is Input
     */
    @Override
    public boolean equals(final Object other) {
        return other instanceof Input;
    }

    /**
     * Returns hash code.
     *
     * All Input instances return the same hash code.
     *
     * @return Hash code
     */
    @Override
    public int hashCode() {
        return 1;
    }
}
