package emission.simple;

import emission.Emission;
import emission.Emitted;

/**
 * Simple pair of updated emission and register name.
 *
 * Immutable value object representing the result of emitting one expression.
 *
 * Example usage:
 * <pre>
 * Emitted result = new SimpleEmitted(emission, "%t0");
 * Emission updated = result.emission();
 * String reg = result.register();
 * </pre>
 */
public final class SimpleEmitted implements Emitted {

    private final Emission em;
    private final String reg;

    /**
     * Primary constructor.
     *
     * @param emission Updated instruction sequence
     * @param register Register or value name where result lives
     */
    public SimpleEmitted(final Emission emission, final String register) {
        this.em = emission;
        this.reg = register;
    }

    @Override
    public Emission emission() {
        return this.em;
    }

    @Override
    public String register() {
        return this.reg;
    }
}
