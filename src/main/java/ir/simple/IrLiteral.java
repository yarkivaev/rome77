package ir.simple;

import ir.Literal;

/**
 * Integer literal in intermediate representation.
 *
 * Immutable value object representing a constant integer value.
 * Roman numerals are already converted to integers at this stage.
 *
 * Example usage:
 * <pre>
 * Literal five = new IrLiteral(5);
 * int value = five.value();
 * </pre>
 */
public final class IrLiteral implements Literal {

    private final int val;

    /**
     * Primary constructor.
     *
     * @param value Integer value
     */
    public IrLiteral(final int value) {
        this.val = value;
    }

    /**
     * Returns the integer value.
     *
     * @return Integer value of this literal
     */
    @Override
    public int value() {
        return this.val;
    }

    /**
     * Checks equality based on value.
     *
     * @param other Object to compare
     * @return True if other is Literal with same value
     */
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Literal)) {
            return false;
        }
        final Literal that = (Literal) other;
        return this.value() == that.value();
    }

    /**
     * Returns hash code based on value.
     *
     * @return Hash code
     */
    @Override
    public int hashCode() {
        return this.value();
    }
}
