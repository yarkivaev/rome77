package ir;

/**
 * Function call expression.
 *
 * Represents function application with arguments.
 *
 * Example usage:
 * <pre>
 * Call call = (Call) expr;
 * String name = call.name();
 * Iterable<Expression> args = call.arguments();
 * </pre>
 */
public interface Call extends Expression {

    /**
     * Returns the function name.
     *
     * @return Function identifier, never null or empty
     */
    String name();

    /**
     * Returns the argument expressions.
     *
     * Arguments are ordered as they appear in source.
     * Empty for zero-argument calls.
     *
     * @return Argument list, never null, may be empty
     */
    Iterable<Expression> arguments();
}
