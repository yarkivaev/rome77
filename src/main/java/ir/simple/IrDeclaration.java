package ir.simple;

import ir.Declaration;
import ir.Expression;
import java.util.Objects;

/**
 * Variable declaration statement in intermediate representation.
 *
 * Immutable value object representing As declaration binding variable to expression.
 *
 * Example usage:
 * <pre>
 * Expression init = new IrLiteral(10);
 * Declaration decl = new IrDeclaration("x", init);
 * String name = decl.name();
 * Expression expr = decl.expression();
 * </pre>
 */
public final class IrDeclaration implements Declaration {

    private final String identifier;
    private final Expression expr;

    /**
     * Primary constructor.
     *
     * @param name Variable name
     * @param expression Initialization expression
     */
    public IrDeclaration(final String name, final Expression expression) {
        this.identifier = name;
        this.expr = expression;
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
     * Returns the initialization expression.
     *
     * @return Expression bound to variable
     */
    @Override
    public Expression expression() {
        return this.expr;
    }

    /**
     * Checks equality based on name and expression.
     *
     * @param other Object to compare
     * @return True if other is Declaration with same name and expression
     */
    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Declaration)) {
            return false;
        }
        final Declaration that = (Declaration) other;
        return this.name().equals(that.name()) &&
            this.expression().equals(that.expression());
    }

    /**
     * Returns hash code based on name and expression.
     *
     * @return Hash code
     */
    @Override
    public int hashCode() {
        return Objects.hash(this.name(), this.expression());
    }
}
