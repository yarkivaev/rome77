package ir.simple;

import ir.Function;
import ir.Program;
import ir.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Program in intermediate representation.
 *
 * Immutable value object representing complete program structure.
 *
 * Example usage:
 * <pre>
 * List&lt;Function&gt; funcs = Arrays.asList(func);
 * List&lt;Statement&gt; stmts = Arrays.asList(
 *     new IrOutput(new IrLiteral(42))
 * );
 * Program prog = new IrProgram(funcs, stmts);
 * </pre>
 */
public final class IrProgram implements Program {

    private final List<Function> fns;
    private final List<Statement> stmts;

    /**
     * Primary constructor.
     *
     * @param functions Function definitions
     * @param statements Main body statements
     */
    public IrProgram(
        final Iterable<Function> functions,
        final Iterable<Statement> statements
    ) {
        this.fns = new ArrayList<>();
        functions.forEach(this.fns::add);
        this.stmts = new ArrayList<>();
        statements.forEach(this.stmts::add);
    }

    /**
     * Returns all function definitions.
     *
     * @return Function definitions
     */
    @Override
    public Iterable<Function> functions() {
        return new ArrayList<>(this.fns);
    }

    /**
     * Returns main body statements.
     *
     * @return Main statements
     */
    @Override
    public Iterable<Statement> statements() {
        return new ArrayList<>(this.stmts);
    }

    /**
     * Checks equality based on functions and statements.
     *
     * @param other Object to compare
     * @return True if other is Program with same structure
     */
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Program)) {
            return false;
        }
        final Program that = (Program) other;
        return this.listOfFunctions(this.functions()).equals(this.listOfFunctions(that.functions())) &&
            this.listOfStatements(this.statements()).equals(this.listOfStatements(that.statements()));
    }

    /**
     * Returns hash code based on functions and statements.
     *
     * @return Hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.listOfFunctions(this.functions()), this.listOfStatements(this.statements()));
    }

    /**
     * Converts functions iterable to list.
     *
     * @param iterable Iterable to convert
     * @return List containing same elements
     */
    private List<Function> listOfFunctions(
        final Iterable<Function> iterable
    ) {
        final List<Function> result = new ArrayList<>();
        iterable.forEach(result::add);
        return result;
    }

    /**
     * Converts statements iterable to list.
     *
     * @param iterable Iterable to convert
     * @return List containing same elements
     */
    private List<Statement> listOfStatements(
        final Iterable<Statement> iterable
    ) {
        final List<Statement> result = new ArrayList<>();
        iterable.forEach(result::add);
        return result;
    }
}
