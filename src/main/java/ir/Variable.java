package ir;

/**
 * Variable reference expression.
 *
 * Represents a reference to a variable or function parameter.
 *
 * Example usage:
 * <pre>
 * Variable var = (Variable) expr;
 * String name = var.name();
 * </pre>
 */
public interface Variable extends Expression {

    /**
     * Returns the variable name.
     *
     * @return Variable identifier, never null or empty
     */
    String name();
}
