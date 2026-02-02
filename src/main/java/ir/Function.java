package ir;

/**
 * Function definition in intermediate representation.
 *
 * Represents a Munus function with name, parameters, and body expression.
 *
 * Example usage:
 * <pre>
 * Function fib = program.functions().iterator().next();
 * String name = fib.name();
 * Iterable<String> params = fib.parameters();
 * Expression body = fib.body();
 * </pre>
 */
public interface Function {

    /**
     * Returns the function name.
     *
     * @return Function identifier, never null or empty
     */
    String name();

    /**
     * Returns the parameter names.
     *
     * Parameters are ordered as declared in function definition.
     * Empty for zero-parameter functions.
     *
     * @return Parameter names, never null, may be empty
     */
    Iterable<String> parameters();

    /**
     * Returns the function body expression.
     *
     * @return Body expression, never null
     */
    Expression body();
}
