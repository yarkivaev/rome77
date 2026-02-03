package ir;

/**
 * Variable declaration statement.
 *
 * Represents As declaration binding variable name to expression.
 *
 * Example usage:
 * <pre>
 * Declaration decl = (Declaration) stmt;
 * String name = decl.name();
 * Expression expr = decl.expression();
 * </pre>
 */
public interface Declaration extends Statement {

    /**
     * Returns the variable name.
     *
     * @return Variable identifier, never null or empty
     */
    String name();

    /**
     * Returns the initialization expression.
     *
     * @return Expression bound to variable, never null
     */
    Expression expression();
}
