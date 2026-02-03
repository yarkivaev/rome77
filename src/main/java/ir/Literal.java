package ir;

/**
 * Integer literal expression.
 *
 * Represents a Roman numeral constant in IR form.
 *
 * Example usage:
 * <pre>
 * Literal five = (Literal) expr;
 * int value = five.value();
 * </pre>
 */
public interface Literal extends Expression {

    /**
     * Returns the integer value.
     *
     * Roman numerals are converted to integers:
     * I=1, V=5, X=10, XIV=14, N=0, etc.
     *
     * @return Integer value of this literal
     */
    int value();
}
