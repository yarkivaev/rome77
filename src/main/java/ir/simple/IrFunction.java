package ir.simple;

import ir.Expression;
import ir.Function;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Function definition in intermediate representation.
 *
 * Immutable value object representing Munus function with parameters and body.
 *
 * Example usage:
 * <pre>
 * List&lt;String&gt; params = Arrays.asList("n");
 * Expression body = new IrBinaryOp(
 *     Operator.ADD,
 *     new IrVariable("n"),
 *     new IrVariable("n")
 * );
 * Function func = new IrFunction("double", params, body);
 * </pre>
 */
public final class IrFunction implements Function {

    private final String identifier;
    private final List<String> params;
    private final Expression expr;

    /**
     * Primary constructor.
     *
     * @param name Function name
     * @param parameters Parameter names
     * @param body Function body expression
     */
    public IrFunction(
        final String name,
        final Iterable<String> parameters,
        final Expression body
    ) {
        this.identifier = name;
        this.params = new ArrayList<>();
        parameters.forEach(this.params::add);
        this.expr = body;
    }

    /**
     * Returns the function name.
     *
     * @return Function identifier
     */
    @Override
    public String name() {
        return this.identifier;
    }

    /**
     * Returns the parameter names.
     *
     * @return Parameter names
     */
    @Override
    public Iterable<String> parameters() {
        return new ArrayList<>(this.params);
    }

    /**
     * Returns the function body expression.
     *
     * @return Body expression
     */
    @Override
    public Expression body() {
        return this.expr;
    }

    /**
     * Checks equality based on name, parameters, and body.
     *
     * @param other Object to compare
     * @return True if other is Function with same structure
     */
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Function)) {
            return false;
        }
        final Function that = (Function) other;
        return this.name().equals(that.name()) &&
            this.listOf(this.parameters()).equals(this.listOf(that.parameters())) &&
            this.body().equals(that.body());
    }

    /**
     * Returns hash code based on name, parameters, and body.
     *
     * @return Hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.name(), this.listOf(this.parameters()), this.body());
    }

    /**
     * Converts iterable to list.
     *
     * @param iterable Iterable to convert
     * @return List containing same elements
     */
    private List<String> listOf(final Iterable<String> iterable) {
        final List<String> result = new ArrayList<>();
        iterable.forEach(result::add);
        return result;
    }
}
