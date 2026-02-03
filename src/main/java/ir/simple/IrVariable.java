package ir.simple;

import ir.Variable;

/**
 * Variable reference in intermediate representation.
 *
 * Immutable value object representing a reference to a variable or function parameter.
 *
 * Example usage:
 * <pre>
 * Variable x = new IrVariable("x");
 * String name = x.name();
 * </pre>
 */
public final class IrVariable implements Variable {

    private final String identifier;

    /**
     * Primary constructor.
     *
     * @param name Variable identifier
     */
    public IrVariable(final String name) {
        this.identifier = name;
    }

    /**
     * Returns the variable name.
     *
     * @return Variable identifier
     */
    @Override
    public String name() {
        return this.identifier;
    }

    /**
     * Checks equality based on name.
     *
     * @param other Object to compare
     * @return True if other is Variable with same name
     */
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Variable)) {
            return false;
        }
        final Variable that = (Variable) other;
        return this.name().equals(that.name());
    }

    /**
     * Returns hash code based on name.
     *
     * @return Hash code
     */
    @Override
    public int hashCode() {
        return this.name().hashCode();
    }
}
