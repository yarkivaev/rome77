package ir.simple;

import ir.Call;
import ir.Expression;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Function call expression in intermediate representation.
 *
 * Immutable value object representing function application with arguments.
 *
 * Example usage:
 * <pre>
 * List&lt;Expression&gt; args = Arrays.asList(new IrLiteral(5));
 * Call call = new IrCall("double", args);
 * String name = call.name();
 * Iterable&lt;Expression&gt; arguments = call.arguments();
 * </pre>
 */
public final class IrCall implements Call {

    private final String identifier;
    private final List<Expression> args;

    /**
     * Primary constructor.
     *
     * @param name Function name
     * @param arguments Argument expressions
     */
    public IrCall(final String name, final Iterable<Expression> arguments) {
        this.identifier = name;
        this.args = new ArrayList<>();
        arguments.forEach(this.args::add);
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
     * Returns the argument expressions.
     *
     * @return Argument list
     */
    @Override
    public Iterable<Expression> arguments() {
        return new ArrayList<>(this.args);
    }

    /**
     * Checks equality based on name and arguments.
     *
     * @param other Object to compare
     * @return True if other is Call with same name and arguments
     */
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Call)) {
            return false;
        }
        final Call that = (Call) other;
        return this.name().equals(that.name()) &&
            this.listOf(this.arguments()).equals(this.listOf(that.arguments()));
    }

    /**
     * Returns hash code based on name and arguments.
     *
     * @return Hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.name(), this.listOf(this.arguments()));
    }

    /**
     * Converts iterable to list.
     *
     * @param iterable Iterable to convert
     * @return List containing same elements
     */
    private List<Expression> listOf(final Iterable<Expression> iterable) {
        final List<Expression> result = new ArrayList<>();
        iterable.forEach(result::add);
        return result;
    }
}
