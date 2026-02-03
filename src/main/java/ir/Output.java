package ir;

/**
 * Output statement.
 *
 * Represents Grafo statement that writes expression value to stdout.
 *
 * Example usage:
 * <pre>
 * Output out = (Output) stmt;
 * Expression expr = out.expression();
 * </pre>
 */
public interface Output extends Statement {

    /**
     * Returns the expression to output.
     *
     * @return Expression to evaluate and print, never null
     */
    Expression expression();
}
